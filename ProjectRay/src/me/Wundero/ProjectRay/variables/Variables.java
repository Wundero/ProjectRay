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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.framework.Format;
import me.Wundero.ProjectRay.utils.Utils;

public class Variables {
	public Object get(String key, Optional<Player> sender, Optional<Player> recipient, Optional<Format> format,
			Optional<TextTemplate> template) {
		Optional<Variable> var = store.getVariable(key);
		if (var.isPresent()) {
			Variable v = var.get();
			Map<Param, Object> map = Utils.sm();
			for (Param p : store.getParams(v)) {
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
				}
			}
			return v.parse(map);
		}
		return "";
	}

	public Variables() {
		store = new Store();
		registerVariable("online", () -> Text.of(Sponge.getServer().getOnlinePlayers().size() + ""));
		registerVariable("player", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.of();
			}
			return Text.of(((Player) objects.get(Param.SENDER)).getName());
		});
		registerVariable("displayname", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.of();
			}
			Player pl = (Player) objects.get(Param.SENDER);
			return pl.get(Keys.DISPLAY_NAME).isPresent() ? pl.get(Keys.DISPLAY_NAME).get() : Text.of(pl.getName());
		});
	}

	private Store store;

	public boolean registerVariable(Variable v) {
		return store.registerVariable(v);
	}

	public boolean registerVariable(String key, Consumer<Map<Param, Object>> task) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Parameters
			@Override
			public Text parse(Map<Param, Object> objects) {
				task.accept(objects);
				return Text.of();
			}

		});
	}

	public boolean registerVariable(String key, Supplier<Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Parameters
			@Override
			public Text parse(Map<Param, Object> objects) {
				return replacer.get();
			}

		});
	}

	public boolean registerVariable(String key, Function<Map<Param, Object>, Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Parameters
			@Override
			public Text parse(Map<Param, Object> objects) {
				return replacer.apply(objects);
			}

		});
	}
}