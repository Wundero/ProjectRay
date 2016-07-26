package me.Wundero.ProjectRay.commands;
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
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.framework.FormatType;
import me.Wundero.ProjectRay.framework.RayPlayer;

public class MessageCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
		if (!sender.hasPermission("ray.message") || !(sender instanceof Player)) {
			sender.sendMessage(Text.builder("You do not have permission to do that!").color(TextColors.RED).build());
			return CommandResult.success();
		}
		Player sendto = (Player) args.getOne("player").get();
		Player sendfrom = (Player) sender;
		String message = (String) args.getOne("message").get();
		MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(
				Cause.source(Ray.get()).named("formattype", FormatType.MESSAGE_SEND).build(),
				sendfrom.getMessageChannel(),
				Optional.of(MessageChannel.combined(MessageChannel.fixed(sendfrom), MessageChannel.TO_CONSOLE)),
				new MessageEvent.MessageFormatter(Text.of("You to ", sendto.getName()), Text.of(message)),
				Text.of(message), false);
		MessageChannelEvent.Chat event2 = SpongeEventFactory.createMessageChannelEventChat(
				Cause.source(Ray.get()).named("formattype", FormatType.MESSAGE_RECEIVE).build(),
				sendto.getMessageChannel(),
				Optional.of(MessageChannel.combined(MessageChannel.fixed(sendto), MessageChannel.TO_CONSOLE)),
				new MessageEvent.MessageFormatter(Text.of(sendfrom.getName(), " to you"), Text.of(message)),
				Text.of(message), false);
		if (!Sponge.getEventManager().post(event) && !Sponge.getEventManager().post(event2)) {
			event.getChannel().get().send(event.getMessage());
			event2.getChannel().get().send(event2.getMessage());
			RayPlayer.getRay(sendto).setLastMessaged(Optional.of(RayPlayer.getRay(sendfrom)), true);
		}
		return CommandResult.success();
	}

}
