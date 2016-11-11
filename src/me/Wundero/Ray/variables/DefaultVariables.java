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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.tag.Tag;
import me.Wundero.Ray.utils.TextUtils;

/**
 * Class that loads default variables that come with the plugin into the store.
 */
public class DefaultVariables {

	/**
	 * Register all default vars and wrappers
	 */
	public static void register(Variables v) {
		v.registerWrapper("stripped", (va, t) -> {
			return TextUtils.strip(t);
		});
		v.registerWrapper("nourls", (va, t) -> {
			return TextUtils.noUrls(t);
		});
		v.registerVariable("quote", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.of();
			}
			Optional<String> quote = RayPlayer.get((Player) objects.get(Param.SENDER)).getQuote();
			if (!quote.isPresent()) {
				return Text.of();
			}
			return TextSerializers.FORMATTING_CODE.deserialize(quote.get());
		});
		v.registerVariable("afk", (objects) -> {
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
			return RayPlayer.get(player).AFK() ? Text.of(TextColors.GRAY, "[AFK] ") : Text.of();
		});
		v.registerVariable("online", () -> Text.of(Sponge.getServer().getOnlinePlayers().size() + ""));
		v.registerVariable("player", (objects) -> {
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
		v.registerVariable("displayname", (objects) -> {
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
		v.registerVariable("sound", (objects) -> {
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
		v.registerVariable("channel", (objects) -> {
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
		v.registerVariable("channelname", (objects) -> {
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
		v.registerVariable("tag", (objects) -> {
			if (!objects.containsKey(Param.PARSABLE) || !objects.containsKey(Param.DATA)) {
				return Text.EMPTY;
			}
			String tagname = objects.get(Param.DATA).toString();
			ParsableData d = (ParsableData) objects.get(Param.PARSABLE);
			// Tag type dec irrelevant because abstract method
			Optional<Tag<?>> t = Ray.get().getTags().get(tagname);
			if (t.isPresent()) {
				return t.get().get(Optional.of(d)).orElse(Text.of());
			}
			return Text.of();
		});
		v.registerVariable("timestamp", () -> {
			Date date = new Date();
			return Text.of(date.toString());
		});
		v.registerVariable("world", (o) -> {
			if (!o.containsKey(Param.SENDER)) {
				return Text.EMPTY;
			}
			Player p = (Player) o.get(Param.SENDER);
			return Text.of(p.getWorld().getName());
		});
		v.registerVariable("balance", (objects) -> {
			if (!objects.containsKey(Param.SENDER)) {
				return Text.EMPTY;
			}
			if (!Ray.get().getEcon().isPresent()) {
				return Text.EMPTY;
			}
			String cur = null;
			if (objects.containsKey(Param.DATA)) {
				cur = objects.get(Param.DATA).toString();
			}
			EconomyService e = Ray.get().getEcon().get();
			Currency c = e.getDefaultCurrency();
			if (cur != null) {
				for (Currency x : e.getCurrencies()) {
					if (x.getName().equalsIgnoreCase(cur)) {
						c = x;
					}
				}
			}
			UniqueAccount acc = e.getOrCreateAccount(((Player) objects.get(Param.SENDER)).getUniqueId())
					.orElseThrow(() -> new NullPointerException("Could not get account"));
			return Text.of(acc.getBalance(c).toPlainString());
		});
	}

}
