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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import me.Wundero.Ray.utils.Utils;

public class ChannelHelpCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> texts = Utils.sl();
		texts.add(cmdgen("channel", "View a list of channels.", "", false, "ch"));
		texts.add(cmdgen("help", "View a list of channel commands.", "", true, "h"));
		texts.add(cmdgen("join", "Join a channel.", "<channel>", true, "j"));
		texts.add(cmdgen("leave", "Leave a channel.", "[channel]", true, "l"));
		texts.add(
				cmdgen("quickmessage", "Message a channel without joining it.", "<channel> <message...>", true, "qm"));
		texts.add(cmdgen("role", "View or edit channel member roles.", "[player] [set <role>]", true, "r"));
		texts.add(cmdgen("who", "View who is in a channel.", "[channel]", true, "w"));
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Channels", TextColors.BLACK,
				"]", " ", TextColors.GRAY, "Channel commands:");
		PaginationService ps = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder b = ps.builder();
		b.contents(texts);
		b.title(Text.of(TextColors.AQUA, TextStyles.BOLD, "Channels", TextColors.GREEN));
		b.header(header).padding(Text.of(TextColors.GREEN, "="));
		b.sendTo(src);
		return CommandResult.success();
	}

	private Text cmdgen(String name, String desc, String args, boolean chprefix, String... aliases) {
		if (chprefix) {
			name = "ch " + name;
		}
		String cmd = "/" + name + " ";
		String cmd2 = cmd;
		String a = "(";
		boolean f = true;
		for (String s : aliases) {
			if (f) {
				f = false;
			} else {
				a += "|";
			}
			a += s;
		}
		a += ")";
		if (!a.equals("()")) {
			cmd += a + " ";
		}
		cmd += args;
		cmd2 += args;
		Text out = Text.builder(cmd).color(TextColors.AQUA).onClick(TextActions.suggestCommand(cmd2))
				.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to use the command!"))).build();
		out = out.concat(Text.of(TextColors.GRAY, " - "));
		out = out.concat(Text.of(TextColors.AQUA, desc));
		return out;
	}

}
