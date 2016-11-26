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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.tag.SelectableTag;
import me.Wundero.Ray.utils.Utils;

/**
 * Command to delete a tag.
 */
public class TagDeleteCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.tags.delete")) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		String tag = args.getOne("tag").get().toString().toLowerCase();
		Optional<SelectableTag> t = Ray.get().getTags().get(tag, Utils.hm(), SelectableTag.class);
		if (!t.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "That is not a valid tag!"));
		}
		SelectableTag ta = t.get();
		Optional<String> name = args.getOne("name");
		if (name.isPresent()) {
			String n = name.get().toLowerCase();
			if (ta.getObject().containsKey(n)) {
				ta.getObject().remove(n);
				src.sendMessage(Text.of(TextColors.AQUA, "Tag " + n + " has been deleted for " + tag + "!"));
				return CommandResult.success();
			} else {
				throw new CommandException(Text.of(TextColors.RED, "That is not a valid tag!"));
			}
		} else {
			Ray.get().getTags().deregister(tag);
			src.sendMessage(Text.of(TextColors.AQUA, "Tag " + tag + " has been deleted!"));
			return CommandResult.success();
		}
	}

}