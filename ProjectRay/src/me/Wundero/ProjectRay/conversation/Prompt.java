package me.Wundero.ProjectRay.conversation;
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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;

public abstract class Prompt {

	// prompt for information - gets a question and displays it, choose from
	// options if you want, parse input and handle it

	protected TextTemplate template;

	public Prompt(TextTemplate template) {
		this.template = template;
	}

	public abstract Text getQuestion(ConversationContext context);

	public abstract Optional<List<Option>> options(ConversationContext context);

	public abstract Text getFailedText(ConversationContext context, String failedInput);

	public abstract Prompt onInput(Optional<Option> selected, String text, ConversationContext context);

	public boolean isInputValid(ConversationContext context, String input) {
		Optional<List<Option>> o = options(context);
		if (o != null && o.isPresent()) {
			List<Option> opts = options(context).get();
			for (Option opt : opts) {
				if (opt.works(input)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public Optional<Option> getSelected(ConversationContext context, String input) {
		Optional<List<Option>> o = options(context);
		if (o != null && o.isPresent()) {
			List<Option> opts = options(context).get();
			for (Option opt : opts) {
				if (opt.works(input)) {
					return Optional.of(opt);
				}
			}
		}
		return Optional.empty();
	}

	public final Prompt input(ConversationContext context, String input) {
		if (isInputValid(context, input)) {
			return onInput(getSelected(context, input), input, context);
		} else {
			context.getHolder().sendMessage(
					((Conversation) context.getData("conversation")).getPrefix().concat(getFailedText(context, input)));
			return this;
		}
	}

	public Text buildList(Optional<List<Option>> opts) {
		if (!opts.isPresent()) {
			return Text.of();
		}
		Text.Builder builder = Text.builder();
		boolean f = true;
		for (Option o : opts.get()) {
			if (!f) {
				builder.append(Text.builder(", ").color(TextColors.GRAY).build());
			} else {
				f = false;
			}
			builder.append(o.getDisplay());
		}
		return builder.build();
	}

	public Text formatTemplate(ConversationContext context) {
		Map<String, Object> args = Utils.sm();
		args.put("options", buildList(options(context)));
		args = Ray.get().setVars(args, template, Optional.of(context.getHolder()), Optional.empty(), Optional.empty(),
				Optional.empty(), false);
		return template.apply(args).build();
	}
}
