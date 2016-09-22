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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
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

public class RayCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Ray", TextColors.BLACK, "]",
				" ", TextColors.GRAY, "Ray commands:");
		List<Text> texts = Utils.sl();
		for (List<String> o : Commands.getChildren().keySet()) {
			texts.add(buildTextForCmd(src, Commands.getChildren().get(o), o));
		}
		PaginationService ps = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder b = ps.builder();
		b.contents(texts);
		b.title(Text.of(TextColors.AQUA, TextStyles.BOLD, "Ray", TextColors.GREEN));
		b.header(header).padding(Text.of(TextColors.GREEN, "="));
		b.sendTo(src);
		return CommandResult.success();
	}

	private Text buildTextForCmd(CommandSource src, CommandCallable cmd, List<String> aliases) {
		Text out = Text.builder("/ray " + aliases.get(0) + " ").color(TextColors.DARK_GREEN)
				.onClick(TextActions.suggestCommand("/ray " + aliases.get(0)))
				.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to use the command!"))).build();
		Optional<Text> opt = cmd.getShortDescription(src);
		out = out.concat(Text.of(TextColors.GRAY, "- "));
		out = out.concat(opt.orElse(Text.of(TextColors.GRAY, "Default command description")));
		if (aliases.size() > 1) {
			out = out.concat(Text.of("\n"));
			out = out.concat(Text.of(TextColors.GRAY, "Aliases: "));
			for (int i = 1; i < aliases.size(); i++) {
				out = out.concat(Text.builder(aliases.get(i) + " ").color(TextColors.DARK_GREEN)
						.onClick(TextActions.suggestCommand("/ray " + aliases.get(i)))
						.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to use the command!"))).build());
			}
		}
		return out;
	}

}
