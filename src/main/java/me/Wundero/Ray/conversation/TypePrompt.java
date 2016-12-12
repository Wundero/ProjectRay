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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

/**
 * Represents a prompt that returns an object of a certain type.
 */
public abstract class TypePrompt<T> extends Prompt {

	// prompt that tries to parse a value from a class - if it does not have the
	// right method, doesn't work

	protected Optional<Class<? extends T>> type = Optional.empty();

	/**
	 * Create a type prompt that will try to validate based on the type class.
	 */
	public TypePrompt(TextTemplate template, Class<? extends T> type) {
		this(template, Optional.ofNullable(type));
	}

	/**
	 * Create a type prompt that will try to validate based on the type class.
	 */
	public TypePrompt(TextTemplate template, Optional<Class<? extends T>> type) {
		super(template);
		this.type = type;
	}

	/**
	 * Checks to see if the input is parsable to the type provided.
	 */
	@Override
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
		} else if (type.isPresent()) {
			Class<? extends T> t = type.get();
			try {
				Method m = null;
				try {
					m = t.getDeclaredMethod("valueOf", String.class);// integer
																		// class,
																		// etc.
				} catch (NoSuchMethodException e1) {
					try {
						m = t.getDeclaredMethod("deserialize", String.class);// serializable
																				// classes
					} catch (Exception e) {
						return false;
					}
				}
				if (m == null) {
					return false;
				}
				boolean a = m.isAccessible();
				m.setAccessible(true);
				Object ob = m.invoke(null, input);// apply method
				m.setAccessible(a);
				if (ob != null) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Return the parsed value from the integer
	 */
	@Override
	public Optional<Option> getSelected(ConversationContext context, String input) {
		// same as above except returns values and not booleans
		if (super.getSelected(context, input).isPresent()) {
			return super.getSelected(context, input);
		} else if (type.isPresent()) {
			Class<? extends T> t = type.get();
			try {
				Method m = null;
				try {
					m = t.getDeclaredMethod("valueOf", String.class);
				} catch (NoSuchMethodException e1) {
					try {
						m = t.getDeclaredMethod("deserialize", String.class);
					} catch (Exception e) {
						return Optional.empty();
					}
				}
				if (m == null) {
					return Optional.empty();
				}
				boolean a = m.isAccessible();
				m.setAccessible(true);
				Object ob = m.invoke(null, input);
				m.setAccessible(a);
				if (ob != null) {
					return Optional.of(new Option("value", Text.of("value"), ob));
				} else {
					return Optional.empty();
				}
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	/**
	 * Return an empty list of options; not necessary for type parsing.
	 */
	@Override
	public Optional<List<Option>> options(ConversationContext context) {
		return Optional.empty();
	}

	/**
	 * Return the next prompt after handling the input.
	 */
	public abstract Prompt onTypeInput(T object, String text, ConversationContext context);

	/**
	 * Return the type class from a string.
	 */
	protected T get(Optional<Option> selected) {
		return type.isPresent() && selected.isPresent() ? type.get().cast(selected.get().getValue()) : null;
	}

	/**
	 * Parse the input and have the extending classes handle the object based
	 * input.
	 */
	@Override
	public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
		return onTypeInput(get(selected), text, context);
	}

}
