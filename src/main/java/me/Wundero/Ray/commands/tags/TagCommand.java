package me.Wundero.Ray.commands.tags;
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
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;

public class TagCommand implements CommandExecutor {

	private static Map<List<String>, CommandCallable> children = Utils.sm();

	static {
		children.put(Utils.al("select"),
				CommandSpec.builder().executor(new TagSelectCommand()).description(Text.of("Select a tag."))
						.arguments(GenericArguments.optional(GenericArguments.string(Text.of("tag"))),
								GenericArguments.optional(GenericArguments.string(Text.of("name"))))
						.permission("ray.tags.select").build());
		children.put(Utils.al("create"), CommandSpec.builder().executor(new TagCreateCommand())
				.description(Text.of("Create a tag.")).permission("ray.tags.create")
				.arguments(GenericArguments.string(Text.of("tag")), GenericArguments.string(Text.of("name"))).build());
		children.put(Utils.al("delete"),
				CommandSpec.builder().executor(new TagDeleteCommand()).permission("ray.tags.delete")
						.description(Text.of("Delete a tag.")).arguments(GenericArguments.string(Text.of("tag")),
								GenericArguments.optional(GenericArguments.string(Text.of("name"))))
						.build());
		children.put(Utils.al("modify", "setdesc", "desc", "mod"),
				CommandSpec.builder().executor(new TagModifyCommand()).permission("ray.tags.modify")
						.arguments(GenericArguments.string(Text.of("tag")), GenericArguments.string(Text.of("name")))
						.description(Text.of("Modify a tag's text.")).build());
	}

	public static Map<List<String>, CommandCallable> getChildren() {
		return children;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.tags")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		PaginationService srv = Ray.get().getPaginationService();
		PaginationList.Builder b = srv.builder();
		Text header = Text.of(TextColors.BLACK, "[", TextColors.AQUA, TextStyles.BOLD, "Tags", TextColors.BLACK, "]",
				" ", TextColors.GRAY, "Tag commands:");
		List<Text> texts = Utils.al();
		for (List<String> o : getChildren().keySet()) {
			texts.add(buildTextForCmd(src, getChildren().get(o), o));
		}
		b.contents(texts);
		b.title(Text.of(TextColors.AQUA, TextStyles.BOLD, "Tags", TextColors.GREEN));
		b.header(header).padding(Text.of(TextColors.GREEN, "="));
		b.sendTo(src);
		return CommandResult.success();
	}

	private Text buildTextForCmd(CommandSource src, CommandCallable cmd, List<String> aliases) {
		Text out = Text.builder("/tag " + aliases.get(0) + " ").color(TextColors.DARK_GREEN)
				.onClick(TextActions.suggestCommand("/tag " + aliases.get(0)))
				.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to use the command!"))).build();
		Optional<Text> opt = cmd.getShortDescription(src);
		out = out.concat(Text.of(TextColors.GRAY, "- "));
		out = out.concat(opt.orElse(Text.of(TextColors.GRAY, "Default command description")));
		if (aliases.size() > 1) {
			out = out.concat(Text.of("\n"));
			out = out.concat(Text.of(TextColors.GRAY, "Aliases: "));
			for (int i = 1; i < aliases.size(); i++) {
				out = out.concat(Text.builder(aliases.get(i) + " ").color(TextColors.DARK_GREEN)
						.onClick(TextActions.suggestCommand("/tag " + aliases.get(i)))
						.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to use the command!"))).build());
			}
		}
		return out;
	}

}