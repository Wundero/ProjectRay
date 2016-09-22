package me.Wundero.Ray.commands;
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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.context.FormatContexts;

public class FakeMessageCommand implements CommandExecutor {

	// THIS DOESN'T WORK YET - NEED TO DIAGNOSE PARAMS

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("type")
				|| FormatContext.fromString(args.<String> getOne("type").get()) == FormatContexts.DEFAULT) {
			src.sendMessage(Text.of(TextColors.RED, "You must specify a proper type!"));
			return CommandResult.success();
		}
		if (!args.hasAny("message")) {
			src.sendMessage(Text.of(TextColors.RED, "You must specify a message!"));
			return CommandResult.success();
		}
		Player target = null;
		if (args.hasAny("p")) {
			target = args.<Player> getOne("player").get();
		} else {
			if (!(src instanceof Player)) {
				src.sendMessage(Text.of(TextColors.RED, "You must be a player to send a fake message as yourself!"));
				return CommandResult.success();
			}
			target = (Player) src;
		}
		String type = args.<String> getOne("type").get();
		String message = args.<String> getOne("message").get();
		MessageChannelEvent.Chat ev2 = SpongeEventFactory.createMessageChannelEventChat(
				Cause.source(Ray.get().getPlugin()).named("formattype", FormatContext.fromString(type)).build(),
				target.getMessageChannel(), Optional.of(target.getMessageChannel()),
				new MessageEvent.MessageFormatter(Text.of(message)), Text.of(message), false);
		Sponge.getEventManager().post(ev2);
		if (!ev2.isCancelled()) {
			ev2.getChannel().get().send(target, ev2.getMessage(), ChatTypes.CHAT);
		}
		return CommandResult.success();
	}

}
