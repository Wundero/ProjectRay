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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class Variables {
	// Doing it the lame way for now
	public Object get(String key, Object... objects) {
		Optional<Variable> var = store.getVariable(key);
		if (var.isPresent()) {
			var.get().parse(objects);
		}
		return "";
	}

	public Variables() {
		store = new Store();
		registerVariable("online", () -> Text.of(Sponge.getServer().getOnlinePlayers().size() + ""));
		registerVariable("player", (objects) -> {
			if (objects[0] == null || !(objects[0] instanceof User)) {
				return Text.of();
			}
			return Text.of(((User) objects[0]).getName());
		});
		registerVariable("displayname", (objects) -> {
			if (objects[0] == null || !(objects[0] instanceof User)) {
				return Text.of();
			}
			return ((Player) objects[0]).get(Keys.DISPLAY_NAME).isPresent()
					? ((Player) objects[0]).get(Keys.DISPLAY_NAME).get() : Text.of(((Player) objects[0]).getName());
		});
	}

	private Store store;

	public boolean registerVariable(Variable v) {
		return store.registerVariable(v);
	}

	public boolean registerVariable(String key, Consumer<Object[]> task) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Object[] objects) {
				task.accept(objects);
				return Text.of();
			}

		});
	}

	public boolean registerVariable(String key, Supplier<Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Object[] objects) {
				return replacer.get();
			}

		});
	}

	public boolean registerVariable(String key, Function<Object[], Text> replacer) {
		return registerVariable(new Variable(key.toLowerCase().trim()) {

			@Override
			public Text parse(Object[] objects) {
				return replacer.apply(objects);
			}

		});
	}
}
