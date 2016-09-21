package me.Wundero.ProjectRay.variables;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.framework.format.Format;
import me.Wundero.ProjectRay.utils.TextUtils;
import me.Wundero.ProjectRay.utils.Utils;

public class Variables {
	// parse for provided data
	public Text get(String key, ParsableData parsedat, Optional<Format> format, Optional<TextTemplate> template) {
		Validate.notNull(parsedat);
		Validate.notEmpty(key);
		Optional<Player> sender = parsedat.getSender();
		Optional<Player> recipient = parsedat.getRecipient();
		Optional<Player> observer = parsedat.getObserver();
		String data = null;
		if (key.contains(":")) {
			String[] ks = key.split(":");
			key = ks[0];
			String s = "";
			boolean b = true;
			for (String a : ks) {
				if (b) {
					b = false;
					continue;
				}
				s += a;
			}
			data = s;
		}
		Optional<Variable> var = store.getVariable(key);
		if (var.isPresent()) {
			Variable v = var.get();
			Map<Param, Object> map = Utils.sm();
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
				return TextUtils.urls(v.parse(map));
			} catch (Exception e) {
				return Text.of();
			}
		}
		return Text.of();
	}

	public Variables() {
		store = new Store();
		// register some default variables
		DefaultVariables.register(this);
	}

	private Store store;

	public boolean registerVariable(Variable v) {
		return store.registerVariable(v);
	}

	// these methods allow you to register with lamdas

	public boolean registerVariable(String key, Runnable task) {
		return registerVariable(key.toLowerCase().trim(), (objects) -> task.run());
	}
	
	// just acts on data
	public boolean registerVariable(String key, Consumer<Map<Param, Object>> task) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Map<Param, Object> objects) {
				task.accept(objects);
				return Text.of();
			}

		});
	}

	// just returns static info
	public boolean registerVariable(String key, Supplier<Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Map<Param, Object> objects) {
				return replacer.get();
			}

		});
	}

	// returns info based on data
	public boolean registerVariable(String key, Function<Map<Param, Object>, Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Map<Param, Object> objects) {
				return replacer.apply(objects);
			}

		});
	}
}
