package me.Wundero.Ray.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.features.ChatFilter;
import me.Wundero.Ray.utils.Utils;

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

/**
 * Toggle whether the chat is being filtered.
 */
public class ChatFilterCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.chatfilter")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		ChatFilter f = Ray.get().getFilter();
		if (args.getOne("value").isPresent()) {
			Optional<Boolean> b = args.getOne("value");
			if (b.isPresent()) {
				if (b.get()) {
					f.filter();
				} else {
					f.ignore();
				}
			}
		} else {
			f.toggle();
		}
		Utils.send(src, Text.of(TextColors.AQUA, "Chat is no" + (f.isFilter() ? "w" : " longer") + " being filtered!"));
		return CommandResult.success();
	}

}
