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

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.conversation.TextEditConversation;
import me.Wundero.Ray.tag.SelectableTag;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;

/**
 * Command to modify the text of a tag.
 */
public class TagModifyCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.tags.modify")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "You must be a player to do this!"));
		}
		String t = args.getOne("tag").get().toString().toLowerCase();
		final String n = args.getOne("name").get().toString().toLowerCase();
		Optional<SelectableTag> ta = Ray.get().getTags().get(t, Utils.hm(), SelectableTag.class);
		if (!ta.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "That is not a valid tag!"));
		}
		final SelectableTag tag = ta.get();
		if (!tag.getObject().containsKey(n)) {
			throw new CommandException(Text.of(TextColors.RED, "That is not a valid tag!"));
		}
		TextTemplate templ = tag.getObject().get(n);
		Text modifiable = TextUtils.convertToText(templ);
		TextEditConversation.start((Player) src, modifiable, text -> {
			TextTemplate to = TextUtils.getVars(text);
			tag.getObject().put(n, to);
		});
		return CommandResult.success();
	}

}
