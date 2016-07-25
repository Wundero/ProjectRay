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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.google.common.collect.Lists;

public class RayCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Ray", TextColors.BLACK, "]",
				" ", TextColors.GRAY, "Ray commands:");
		List<Text> texts = Lists.newArrayList();
		texts.add(header);
		for (List<String> o : Commands.getChildren().keySet()) {
			texts.add(buildTextForCmd(src, Commands.getChildren().get(o), o));
		}
		src.sendMessages(texts);
		return CommandResult.success();
	}

	private Text buildTextForCmd(CommandSource src, CommandCallable cmd, List<String> aliases) {
		Text out = Text.of(TextColors.DARK_GREEN, "  /" + aliases.get(0), " ");
		@SuppressWarnings("unchecked")
		Optional<Text> opt = (Optional<Text>) cmd.getShortDescription(src);
		out = out.concat(opt.orElse(Text.of(TextColors.GRAY, "Default command description")));
		if (aliases.size() > 1) {
			out = out.concat(Text.of("\n"));
			out = out.concat(Text.of(TextColors.GRAY, "    Aliases: "));
			for (int i = 1; i < aliases.size(); i++) {
				out = out.concat(Text.of(TextColors.DARK_GREEN, aliases.get(i)));
			}
		}
		return out;
	}

}
