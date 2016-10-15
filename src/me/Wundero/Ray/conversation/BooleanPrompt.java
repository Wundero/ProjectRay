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
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.utils.Utils;

/**
 * Prompt that will return a boolean value based on parsed input.
 */
public abstract class BooleanPrompt extends Prompt {

	private boolean useunicode = true;

	/**
	 * Create a new boolean prompt. unicode boolean is for the yes/no buttons
	 * options
	 */
	public BooleanPrompt(TextTemplate template, boolean unicode) {
		super(template);
		this.useunicode = unicode;
	}

	/**
	 * Create a new boolean prompt. Unicode is set to true.
	 */
	public BooleanPrompt(TextTemplate template) {
		this(template, true);
	}

	/**
	 * Return text when the input fails validation
	 */
	@Override
	public Text getFailedText(ConversationContext context, String failedInput) {
		return Text.of(TextColors.RED, "That is not a valid input! Try yes or no.");
	}

	/**
	 * Check to see if input matches valid booleans
	 */
	@Override
	public boolean isInputValid(ConversationContext context, String input) {
		switch (input.toLowerCase().trim()) {
		case "y":
		case "yes":
		case "n":
		case "no":
		case "true":
		case "t":
		case "f":
		case "false":
		case "0":
		case "1":
			return true;
		default:
			return false;
		}
	}

	/**
	 * Return a boolean value for the input
	 */
	private boolean parseInput(String input) {
		switch (input.toLowerCase().trim()) {
		case "y":
		case "yes":
		case "true":
		case "t":
		case "1":
			return true;
		default:
			return false;
		}
	}

	/**
	 * Return yes/no options
	 */
	@Override
	public Optional<List<Option>> options(ConversationContext context) {
		List<Option> list = Utils.sl();
		list.add(new Option("yes", Text.builder("[" + (useunicode ? '\u2713' : 'Y') + "]").color(TextColors.GREEN)
				.onClick(TextActions.runCommand("y")).build(), true));
		list.add(new Option("no", Text.builder("[" + (useunicode ? '\u2715' : 'N') + "]").color(TextColors.RED)
				.onClick(TextActions.runCommand("n")).build(), false));
		return Optional.of(list);
	}

	/**
	 * Return the next prompt based on the input
	 */
	public abstract Prompt onBooleanInput(boolean value, String text, ConversationContext context);

	/**
	 * Handle input through extending classes
	 */
	@Override
	public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
		return onBooleanInput(selected.isPresent() ? (boolean) selected.get().getValue() : parseInput(text), text,
				context);
	}

}
