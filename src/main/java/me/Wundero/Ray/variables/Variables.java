package me.Wundero.Ray.variables;
/*
 The MIT License (MIT)

 Copyright (c) 2016 Wundero

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.inject.Singleton;

import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.format.StaticFormat;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;

/**
 * Variable parser and singleton storage
 */
@Singleton
public class Variables {

	/**
	 * Parse a string as a key for a variable.
	 */
	public Text get(String key, ParsableData parsedat, Optional<Format> format, Optional<TextTemplate> template) {
		Validate.notNull(parsedat);
		Validate.notEmpty(key);
		final String k2 = key;
		Optional<Player> sender = parsedat.getSender();
		Optional<Player> recipient = parsedat.getRecipient();
		Optional<Player> observer = parsedat.getObserver();
		boolean ch = parsedat.isClickHover();
		Function<Text, Text> formatVar = t -> t;
		if (format.isPresent()) {
			Optional<StaticFormat> intern = format.get().getInternal(StaticFormat.class, parsedat.getPage());
			if (intern.isPresent()) {
				final StaticFormat in = intern.get();
				formatVar = t -> in.formatVariable(k2, t);
			}
		}
		String data = null;
		String[] decdata = null;
		if (key.contains(":")) {
			String[] ks = key.split(":");
			decdata = key.split(":"); // avoiding same pointer - safer
			key = ks[0];
			String s = "";
			String fgx = "";
			boolean b = true;
			for (String a : ks) {
				if (b) {
					b = false;
					continue;
				}
				s += fgx + a;
				fgx = " ";
			}
			data = s;
		}
		Optional<Variable> var = store.getVariable(key);
		if (var.isPresent()) {
			Variable v = var.get();
			Map<Param, Object> map = Utils.hm();
			for (Param p : Param.values()) {
				// add all params that are available to map
				switch (p) {
				case SENDER:
					if (!sender.isPresent()) {
						break;
					}
					map.put(p, sender.get());
					break;
				case RECIPIENT:
					if (!recipient.isPresent()) {
						break;
					}
					map.put(p, recipient.get());
					break;
				case FORMAT:
					if (!format.isPresent()) {
						break;
					}
					map.put(p, format.get());
					break;
				case TEMPLATE:
					if (!template.isPresent()) {
						break;
					}
					map.put(p, template.get());
					break;
				case DATA:
					if (data == null) {
						break;
					}
					map.put(p, data);
					break;
				case OBSERVER:
					if (!observer.isPresent()) {
						break;
					}
					map.put(p, observer.get());
					break;
				case PARSABLE:
					map.put(p, parsedat);
					break;
				}
			}
			try {
				return filterCH(TextUtils.urls(v.parse(map)), ch, formatVar);
			} catch (Exception e) {
				return Text.of();
			}
		} else if (store.getWrapper(key).isPresent() && data != null) {
			VariableWrapper v = store.getWrapper(key).get();
			Optional<Variable> vx = store.getVariable(decdata[1]);
			if (vx.isPresent()) {
				data = data.substring(data.indexOf(":"));
				Map<Param, Object> map = Utils.hm();
				for (Param p : Param.values()) {
					// add all params that are available to map
					switch (p) {
					case SENDER:
						if (!sender.isPresent()) {
							break;
						}
						map.put(p, sender.get());
						break;
					case RECIPIENT:
						if (!recipient.isPresent()) {
							break;
						}
						map.put(p, recipient.get());
						break;
					case FORMAT:
						if (!format.isPresent()) {
							break;
						}
						map.put(p, format.get());
						break;
					case TEMPLATE:
						if (!template.isPresent()) {
							break;
						}
						map.put(p, template.get());
						break;
					case DATA:
						if (data == null) {
							break;
						}
						map.put(p, data);
						break;
					case OBSERVER:
						if (!observer.isPresent()) {
							break;
						}
						map.put(p, observer.get());
						break;
					case PARSABLE:
						map.put(p, parsedat);
						break;
					}
				}
				try {
					return filterCH(TextUtils.urls(v.parse(vx.get(), vx.get().parse(map))), ch, formatVar);
				} catch (Exception e) {
					return Text.of();
				}
			}
		}
		return Text.of();
	}

	private static Text filterCH(Text t, boolean ch, Function<Text, Text> fv) {
		t = fv.apply(t);
		if (ch) {
			return t;
		} else {
			return t.toBuilder().onClick(null).onHover(null).onShiftClick(null).build();
		}
	}

	/**
	 * Instantiate the singleton instance
	 */
	public Variables() {
		store = new Store();
		// register some default variables
		DefaultVariables.register(this);
	}

	private Store store;

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(VariableWrapper w) {
		return store.registerWrapper(w);
	}

	/**
	 * Register a new variable
	 */
	public boolean registerVariable(Variable v) {
		return store.registerVariable(v);
	}

	// these methods allow you to register with lamdas

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, Runnable r) {
		return registerWrapper(key, (BiConsumer<Variable, Text>) (v, t) -> {
			r.run();
		}, 0);
	}

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, Supplier<Text> s) {
		return registerWrapper(key, (BiFunction<Variable, Text, Text>) (v, t) -> s.get());
	}

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, Consumer<Text> c, int useless) {
		return registerWrapper(key, (va, t) -> c.accept(t), useless);
	}

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, Function<Text, Text> f, int useless) {
		return registerWrapper(key, (va, t) -> {
			return f.apply(t);
		});
	}

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, BiConsumer<Variable, Text> c, int useless) {
		return registerWrapper(key, (BiFunction<Variable, Text, Text>) (v, t) -> {
			c.accept(v, t);
			return t;
		});
	}

	/**
	 * Register a new variable wrapper
	 */
	public boolean registerWrapper(String key, BiFunction<Variable, Text, Text> funct) {
		return registerWrapper(new VariableWrapper(key) {

			@Override
			public Text parse(Variable v, Text returned) {
				return funct.apply(v, returned);
			}
		});
	}

	/**
	 * Register a new variable
	 */
	public boolean registerVariable(String key, Runnable task) {
		return registerVariable(key.toLowerCase().trim(), (Consumer<Map<Param, Object>>) (objects) -> task.run());
	}

	// just acts on data

	/**
	 * Register a new variable
	 */
	public boolean registerVariable(String key, Consumer<Map<Param, Object>> task) {
		return registerVariable(key, (Function<Map<Param, Object>, Text>) (o) -> {
			task.accept(o);
			return Text.EMPTY;
		});
	}

	// just returns static info

	/**
	 * Register a new variable
	 */
	public boolean registerVariable(String key, Supplier<Text> replacer) {
		return registerVariable(key, (Function<Map<Param, Object>, Text>) (o) -> replacer.get());
	}

	// returns info based on data

	/**
	 * Register a new variable
	 */
	public boolean registerVariable(String key, Function<Map<Param, Object>, Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Map<Param, Object> objects) {
				return replacer.apply(objects);
			}

		});
	}
}
