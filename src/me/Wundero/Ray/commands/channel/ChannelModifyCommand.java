package me.Wundero.Ray.commands.channel;
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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.framework.channel.Role;
import me.Wundero.Ray.utils.Utils;

/**
 * A command to modify settings of a channel.
 */
public class ChannelModifyCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.channel.modify")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		String ch = args.getOne("channel")
				.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must specify a channel!")))
				.toString();
		if (!args.hasAny("property")) {
			src.sendMessage(Text.of(TextColors.RED, "You must specify a property!"));
			src.sendMessage(Text.of(TextColors.AQUA, "Available properties:"));
			src.sendMessages(proptexts("tag", "permission", "range", "password", "hidden", "autojoin", "default-role",
					"obfuscate-range"));
		}
		String key = args.getOne("property")
				.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must specify a property!")))
				.toString();
		ChatChannel c = Ray.get().getChannels().getChannel(ch);
		if (c == null) {
			throw new CommandException(Text.of(TextColors.RED, "That is not a valid channel!"));
		}
		Optional<String> value = args.getOne("value");
		boolean reset = false;
		switch (key.toLowerCase().trim()) {
		case "tag":
			c.setTag(value.orElse(null));
			reset = true;
			break;
		case "permission":
			c.setPermission(value.orElse(null));
			reset = true;
			break;
		case "range":
			if (value.isPresent()) {
				c.setRange(getFrom(value.get(), -1));
			}
			break;
		case "pass":
		case "passphrase":
		case "password":
			c.setPassword(value.orElse(null));
			reset = true;
			break;
		case "hidden":
			c.setHidden(getFrom(value.orElse(""), false));
			reset = true;
			break;
		case "autojoin":
			c.setAutojoin(getFrom(value.orElse(""), false));
			reset = true;
			break;
		case "default-role":
		case "defaultrole":
		case "defrole":
		case "role":
			c.setDefaultRole(Role.valueOf(value.orElse("GUEST").toUpperCase().trim()));
			reset = true;
			break;
		case "obfuscaterange":
		case "obfuscatedrange":
		case "obfuscated-range":
		case "obfuscate":
		case "obfuscated":
		case "obfuscate-range":
			c.setObfuscateRanged(getFrom(value.orElse(""), false));
			reset = true;
			break;
		}
		boolean rr = reset && !value.isPresent();
		String x = rr ? "reset" : (!value.isPresent() ? "not changed" : "set to " + value.get());
		Text t = Text.of(TextColors.AQUA, "The value of " + key + " was " + x + ".");
		src.sendMessage(t);
		return CommandResult.success();
	}

	private static List<Text> proptexts(String... strings) {
		List<Text> out = Utils.al();
		for (String s : strings) {
			out.add(Text.of(TextColors.GOLD, s));
		}
		return out;
	}

	private static double getFrom(String s, double def) {
		try {
			return Double.valueOf(s);
		} catch (Exception e) {
			return def;
		}
	}

	private static boolean getFrom(String s, boolean def) {
		switch (s.toLowerCase().trim()) {
		case "y":
		case "yes":
		case "true":
		case "t":
		case "1":
			return true;
		case "n":
		case "no":
		case "f":
		case "false":
		case "0":
			return false;
		default:
			return def;
		}
	}

}
