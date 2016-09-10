package me.Wundero.ProjectRay.commands.channel;
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

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.framework.channel.ChatChannel;
import me.Wundero.ProjectRay.framework.player.RayPlayer;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;

public class ChannelWhoCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Optional<String> cn = args.<String>getOne("channel");
		if (!cn.isPresent() && !(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "You must specify a channel!"));
			return CommandResult.success();
		}
		ChatChannel c = null;
		if (!cn.isPresent()) {
			Player sender = (Player) src;
			c = RayPlayer.get(sender).getActiveChannel();
		} else {
			String ch = cn.get();
			c = Ray.get().getChannels().getChannel(ch, true);
		}
		if (c == null) {
			src.sendMessage(Text.of(TextColors.RED, "That is not a valid channel!"));
			return CommandResult.success();
		}
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Channels", TextColors.BLACK,
				"]", " ", TextColors.GRAY, "Players in channel " + c.getName() + ":");
		List<Text> texts = Utils.sl();
		for (MessageReceiver m : c.getMembers()) {
			if (m instanceof Player) {
				Object v = Ray.get().getVariables().get("displayname", new ParsableData().withSender((Player) m),
						Optional.empty(), Optional.empty());
				Text t;
				if (v instanceof Text) {
					t = (Text) v;
				} else {
					t = Text.of(v.toString());
				}
				t = t.concat(Text.of(TextColors.GRAY, ":", TextColors.GOLD,
						StringUtils.capitalize(c.getMembersCollection().get(m).getRole().name().toLowerCase())));
				texts.add(t);
			}
		}
		PaginationService ps = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder b = ps.builder();
		b.contents(texts);
		b.title(Text.of(TextColors.AQUA, TextStyles.BOLD, "Players", TextColors.GREEN));
		b.header(header).padding(Text.of(TextColors.GREEN, "="));
		b.sendTo(src);
		return CommandResult.success();
	}

}
