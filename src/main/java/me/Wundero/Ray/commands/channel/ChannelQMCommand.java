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
import me.Wundero.Ray.framework.channel.ChatChannel;

/**
 * Message a channel without joining it.
 */
public class ChannelQMCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "You must be a player to do this!"));
		}
		if (!args.getOne("channel").isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "You must choose a channel to message!"));
		}
		String chName = (String) args.getOne("channel").get();
		ChatChannel c = Ray.get().getChannels().getChannel(chName, true);
		if (c == null) {
			throw new CommandException(Text.of(TextColors.RED, "That is not a valid channel!"));
		}
		String message = (String) args.getOne("message").get();
		MessageChannelEvent.Chat e = SpongeEventFactory.createMessageChannelEventChat(
				Cause.source(Ray.get().getPlugin()).owner((Player) src).build(), c, Optional.of(c),
				new MessageEvent.MessageFormatter(Text.of(src.getName(), Text.of(message))), Text.of(message), false);
		if (!Sponge.getEventManager().post(e)) {
			e.getChannel().get().send(src, e.getMessage(), ChatTypes.CHAT);
		}
		return CommandResult.success();
	}

}
