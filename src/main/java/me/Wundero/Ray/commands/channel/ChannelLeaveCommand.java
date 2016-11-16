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
 * Stop listening to a channel. If you are speaking to the channel, that will
 * also cease. If no channel is specified, the current speaking channel is used.
 */
public class ChannelLeaveCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to do this."));
			return CommandResult.success();
		}
		Player s = (Player) src;
		Optional<Object> cn = args.getOne("channel");
		ChatChannel ch = RayPlayer.get(s).getActiveChannel();
		if (cn.isPresent()) {
			ch = Ray.get().getChannels().getChannel((String) cn.get(), true);
		}
		ch.removeMember(s.getUniqueId());
		RayPlayer.get(s).removeListenChannel(ch);
		s.sendMessage(Text.of(TextColors.AQUA, "You have left channel " + ch.getName() + "."));
		if (RayPlayer.get(s).getActiveChannel().getName().equals(ch.getName())) {
			ChatChannel c2 = null;
			for (ChatChannel c : Ray.get().getChannels().getChannels(RayPlayer.get(s).getListenChannels(), false)) {
				if (c.getName().equals(ch.getName())) {
					continue;// shouldn't happen but failsafe
				}
				if (c2 == null) {
					c2 = c;
				} else if (c.compareTo(c2) > 0) {
					c2 = c;
				}
			}
			RayPlayer.get(s).setActiveChannel(c2);
			s.sendMessage(Text.of(TextColors.AQUA, "You will now speak into channel " + c2.getName() + "."));
		}
		return CommandResult.success();
	}

}
