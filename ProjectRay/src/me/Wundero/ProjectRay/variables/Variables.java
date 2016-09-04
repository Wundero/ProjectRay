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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.framework.RayPlayer;
import me.Wundero.ProjectRay.framework.channel.ChatChannel;
import me.Wundero.ProjectRay.framework.format.Format;
import me.Wundero.ProjectRay.utils.Utils;

public class Variables {
	// parse for provided data
	public Object get(String key, Optional<Player> sender, Optional<Player> recipient, Optional<Format> format,
			Optional<TextTemplate> template, Optional<Player> observer) {
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
				}
			}
			return Utils.urlsIf(v.parse(map));
		}
		return "";
	}

	public Variables() {
		store = new Store();
		// register some default variables
		registerVariable("online", () -> Text.of(Sponge.getServer().getOnlinePlayers().size() + ""));
		registerVariable("player", (objects) -> {
			Param playerToUse = Param.SENDER;
			Player player = null;
			if (objects.containsKey(Param.DATA)) {
				String data = (String) objects.get(Param.DATA);
				switch (data) {
				case "sender":
					break;
				case "recipient":
				case "recip":
					playerToUse = Param.RECIPIENT;
					break;
				case "observer":
				case "killer":
					playerToUse = Param.OBSERVER;
					break;
				default:
					playerToUse = Param.DATA;
					Optional<Player> po = Sponge.getServer().getPlayer(data);
					if (!po.isPresent()) {
						return Text.of();
					}
					player = po.get();
				}
			}
			if (!objects.containsKey(playerToUse)) {
				return Text.of();
			}
			if (player == null) {
				player = (Player) objects.get(playerToUse);
			}
			if (player == null) {
				return Text.of();
			}
			return Text.of(player.getName());
		});
		registerVariable("displayname", (objects) -> {
			Param playerToUse = Param.SENDER;
			Player player = null;
			if (objects.containsKey(Param.DATA)) {
				String data = (String) objects.get(Param.DATA);
				switch (data) {
				case "sender":
					break;
				case "recipient":
				case "recip":
					playerToUse = Param.RECIPIENT;
					break;
				case "observer":
				case "killer":
					playerToUse = Param.OBSERVER;
					break;
				default:
					playerToUse = Param.DATA;
					Optional<Player> po = Sponge.getServer().getPlayer(data);
					if (!po.isPresent()) {
						return Text.of();
					}
					player = po.get();
				}
			}
			if (!objects.containsKey(playerToUse)) {
				return Text.of();
			}
			if (player == null) {
				player = (Player) objects.get(playerToUse);
			}
			if (player == null) {
				return Text.of();
			}
			return player.get(Keys.DISPLAY_NAME).isPresent() ? player.get(Keys.DISPLAY_NAME).get()
					: Text.of(player.getName());
		});
		registerVariable("sound", (objects) -> {
			if (!objects.containsKey(Param.RECIPIENT) || !objects.containsKey(Param.DATA)) {
				return;
			}
			Player sender = (Player) objects.get(Param.RECIPIENT);
			String soundname = (String) objects.get(Param.DATA);
			soundname = soundname.replace(" ", "_").toUpperCase();
			Class<?> sts = SoundTypes.class;
			SoundType type = null;
			for (Field f : sts.getDeclaredFields()) {
				if (f.getName().equals(soundname)) {
					try {
						boolean a = f.isAccessible();
						f.setAccessible(true);
						type = (SoundType) f.get(null);
						f.setAccessible(a);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			sender.playSound(type, sender.getLocation().getPosition(), 1.0);
		});
		registerVariable("channel", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.of();
			}
			Player pl = (Player) objects.get(Param.SENDER);
			RayPlayer p = RayPlayer.get(pl);
			ChatChannel c = p.getActiveChannel();
			if (c == null) {
				return Text.of();
			}
			if (c.getTag() == null) {
				return Text.of(c.getName());
			}
			return c.getTag();
		});
		registerVariable("channelname", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.of();
			}
			Player pl = (Player) objects.get(Param.SENDER);
			RayPlayer p = RayPlayer.get(pl);
			ChatChannel c = p.getActiveChannel();
			if (c == null) {
				return Text.of();
			}
			return Text.of(c.getName());
		});
	}

	private Store store;

	public boolean registerVariable(Variable v) {
		return store.registerVariable(v);
	}

	// these methods allow you to register with lamdas

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
