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
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.format.context.FormatContexts;
import me.Wundero.Ray.utils.TextUtils;

/**
 * Execute the "broadcast" format context.
 */
public class BroadcastCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.broadcast")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		String msg = (String) args.getOne("message")
				.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must specify a message!")));
		Text trans = TextUtils.trans(msg);

		MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(
				Cause.source(Ray.get().getPlugin()).named("formatcontext", FormatContexts.BROADCAST).named("sender", src)
						.build(),
				MessageChannel.TO_ALL, Optional.of(MessageChannel.TO_ALL),
				new MessageEvent.MessageFormatter(
						Text.of(TextColors.BLACK, "[", TextColors.GREEN, "Broadcast", TextColors.BLACK, "]"), trans),
				trans, false);
		if (!Sponge.getEventManager().post(event)) {
			event.getChannel().orElse(event.getOriginalChannel()).send(src, event.getMessage());
		}
		return CommandResult.success();
	}
}
