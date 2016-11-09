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
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.utils.Utils;

/**
 * List all channels
 */
public class ChannelCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to do this!"));
			return CommandResult.builder().successCount(0).build();
		}
		Player sender = (Player) src;
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Channels", TextColors.BLACK,
				"]", " ", TextColors.GRAY, "Available channels:");
		List<Text> texts = Utils.al();
		for (ChatChannel channel : Ray.get().getChannels().getJoinableChannels(sender, false)) {
			texts.add(buildForChannel(channel, sender));
		}
		PaginationService ps = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder b = ps.builder();
		b.contents(texts);
		b.title(Text.of(TextColors.AQUA, TextStyles.BOLD, "Channels", TextColors.GREEN));
		b.header(header).padding(Text.of(TextColors.GREEN, "="));
		b.sendTo(src);
		return CommandResult.success();
	}

	private Text buildForChannel(ChatChannel channel, Player source) {
		Text out = Text.of();
		out = out.concat(channel.getTag());
		out = out.concat(Text.of(TextColors.AQUA, " " + channel.getName()));
		TextColor c = TextColors.AQUA;
		if (channel.hasMember(source.getUniqueId())) {
			c = TextColors.GOLD;
		}
		out = out.concat(Text.of(TextColors.GRAY, ": ", c, channel.getSize()));
		return out;
	}

}
