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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.framework.player.RayPlayer;

/**
 * Start talking to a channel. If the channel is not being listened to, listen
 * to that channel.
 */
public class ChannelJoinCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource arg0, CommandContext arg1) throws CommandException {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(Text.of(TextColors.RED, "You must be a player to join a channel!"));
			return CommandResult.success();
		}
		Player s = (Player) arg0;
		Optional<Object> channelName = arg1.getOne("channel");
		if (!channelName.isPresent()) {
			s.sendMessage(Text.of(TextColors.RED, "You must choose a channel to join."));
			return CommandResult.success();
		}
		String ch = (String) channelName.get();
		ChatChannel c = Ray.get().getChannels().getChannel(ch, true);
		if (c == null) {
			s.sendMessage(Text.of(TextColors.RED, "That is not a valid channel!"));
			return CommandResult.success();
		}
		if (!c.canJoin(s)) {
			s.sendMessage(Text.of(TextColors.RED, "You cannot join that channel!"));
			return CommandResult.success();
		}
		if (!RayPlayer.get(s).listeningTo(c)) {
			c.addMember(s);
			RayPlayer.get(s).addListenChannel(c);
		}
		RayPlayer.get(s).setActiveChannel(c);
		s.sendMessage(Text.of(TextColors.AQUA, "You are now speaking to channel " + c.getName() + "."));
		return CommandResult.success();
	}

}
