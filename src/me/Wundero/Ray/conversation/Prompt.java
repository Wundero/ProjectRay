package me.Wundero.Ray.conversation;
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

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;

/**
 * The class that represents a question or statement sent to the player in any
 * part of the conversation
 */
public abstract class Prompt {

	// prompt for information - gets a question and displays it, choose from
	// options if you want, parse input and handle it

	protected TextTemplate template;

	/**
	 * Create a new prompt with a text template to send
	 */
	public Prompt(TextTemplate template) {
		this.template = template;
	}

	/**
	 * Get the internal template
	 */
	public final TextTemplate getTemplate() {
		return template;
	}

	/**
	 * Get the question to send to the player
	 */
	public abstract Text getQuestion(ConversationContext context);

	/**
	 * Get the list of options the player can choose from. If Optional.empty()
	 * is returned, no input validation will be considered by default.
	 */
	public abstract Optional<List<Option>> options(ConversationContext context);

	/**
	 * If input validation fails, the message to send to the player
	 */
	public abstract Text getFailedText(ConversationContext context, String failedInput);

	/**
	 * When input is validated, handle the input as the plugin
	 * 
	 * @return the next prompt to load. Return null to end the conversation.
	 */
	public abstract Prompt onInput(Optional<Option> selected, String text, ConversationContext context);

	/**
	 * Validate the input.
	 */
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

	/**
	 * Get the selected option, if available.
	 */
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

	/**
	 * Send input to the prompt.
	 */
	public final Prompt input(ConversationContext context, String input) {
		if (isInputValid(context, input)) {
			return onInput(getSelected(context, input), input, context);
		} else {
			context.getHolder().sendMessage(((Conversation) context.getData("conversation")).getPrefix()
					.concat(getFailedText(context, input).toBuilder().color(TextColors.RED).build()));
			return this;
		}
	}

	/**
	 * Build a Text object from the list of options
	 */
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

	/**
	 * Use built in variable parsing for the template. Adds {options} variable
	 * (use TextTemplate.arg("options")) which will list all of the options
	 * available.
	 */
	public Text formatTemplate(ConversationContext context) {
		Map<String, Object> args = Utils.sm();
		args.put("options", buildList(options(context)));
		args = Ray.get().setVars(args, template, Optional.of(context.getHolder()), Optional.empty(), Optional.empty(),
				Optional.empty(), false);
		return template.apply(args).build();
	}
}
