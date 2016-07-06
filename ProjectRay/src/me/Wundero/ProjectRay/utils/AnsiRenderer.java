package me.Wundero.ProjectRay.utils;

/*
 * Copyright (C) 2009 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Locale;

import me.Wundero.ProjectRay.utils.Ansi.Attribute;
import me.Wundero.ProjectRay.utils.Ansi.AnsiColor;

/**
 * Renders ANSI color escape-codes in strings by parsing out some special syntax
 * to pick up the correct fluff to use.
 *
 * <p/>
 * The syntax for embedded ANSI codes is:
 *
 * <pre>
 *   <tt>@|</tt><em>code</em>(<tt>,</tt><em>code</em>)* <em>text</em><tt>|@</tt>
 * </pre>
 *
 * Examples:
 *
 * <pre>
 *   <tt>@|bold Hello|@</tt>
 * </pre>
 *
 * <pre>
 *   <tt>@|bold,red Warning!|@</tt>
 * </pre>
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.1
 */
public class AnsiRenderer {
	public static final String BEGIN_TOKEN = "@|";

	private static final int BEGIN_TOKEN_LEN = 2;

	public static final String END_TOKEN = "|@";

	private static final int END_TOKEN_LEN = 2;

	public static final String CODE_TEXT_SEPARATOR = " ";

	public static final String CODE_LIST_SEPARATOR = ",";

	static public String render(final String input) throws IllegalArgumentException {
		StringBuffer buff = new StringBuffer();

		int i = 0;
		int j, k;

		while (true) {
			j = input.indexOf(BEGIN_TOKEN, i);
			if (j == -1) {
				if (i == 0) {
					return input;
				} else {
					buff.append(input.substring(i, input.length()));
					return buff.toString();
				}
			} else {
				buff.append(input.substring(i, j));
				k = input.indexOf(END_TOKEN, j);

				if (k == -1) {
					return input;
				} else {
					j += BEGIN_TOKEN_LEN;
					String spec = input.substring(j, k);

					String[] items = spec.split(CODE_TEXT_SEPARATOR, 2);
					if (items.length == 1) {
						return input;
					}
					String replacement = render(items[1], items[0].split(CODE_LIST_SEPARATOR));

					buff.append(replacement);

					i = k + END_TOKEN_LEN;
				}
			}
		}
	}

	static private String render(final String text, final String... codes) {
		Ansi ansi = Ansi.ansi();
		for (String name : codes) {
			Code code = Code.valueOf(name.toUpperCase(Locale.ENGLISH));

			if (code.isColor()) {
				if (code.isBackground()) {
					ansi = ansi.bg(code.getColor());
				} else {
					ansi = ansi.fg(code.getColor());
				}
			} else if (code.isAttribute()) {
				ansi = ansi.a(code.getAttribute());
			}
		}

		return ansi.a(text).reset().toString();
	}

	public static boolean test(final String text) {
		return text != null && text.contains(BEGIN_TOKEN);
	}

	public static enum Code {

		// Colors
		BLACK(AnsiColor.BLACK), RED(AnsiColor.RED), GREEN(AnsiColor.GREEN), YELLOW(AnsiColor.YELLOW), BLUE(
				AnsiColor.BLUE), MAGENTA(AnsiColor.MAGENTA), CYAN(AnsiColor.CYAN), WHITE(AnsiColor.WHITE),

		// Foreground AnsiColors
		FG_BLACK(AnsiColor.BLACK, false), FG_RED(AnsiColor.RED, false), FG_GREEN(AnsiColor.GREEN, false), FG_YELLOW(
				AnsiColor.YELLOW, false), FG_BLUE(AnsiColor.BLUE, false), FG_MAGENTA(AnsiColor.MAGENTA,
						false), FG_CYAN(AnsiColor.CYAN, false), FG_WHITE(AnsiColor.WHITE, false),

		// Background AnsiColors
		BG_BLACK(AnsiColor.BLACK, true), BG_RED(AnsiColor.RED, true), BG_GREEN(AnsiColor.GREEN, true), BG_YELLOW(
				AnsiColor.YELLOW, true), BG_BLUE(AnsiColor.BLUE, true), BG_MAGENTA(AnsiColor.MAGENTA,
						true), BG_CYAN(AnsiColor.CYAN, true), BG_WHITE(AnsiColor.WHITE, true),

		// Attributes
		RESET(Attribute.RESET), INTENSITY_BOLD(Attribute.INTENSITY_BOLD), INTENSITY_FAINT(
				Attribute.INTENSITY_FAINT), ITALIC(Attribute.ITALIC), UNDERLINE(Attribute.UNDERLINE), BLINK_SLOW(
						Attribute.BLINK_SLOW), BLINK_FAST(Attribute.BLINK_FAST), BLINK_OFF(
								Attribute.BLINK_OFF), NEGATIVE_ON(Attribute.NEGATIVE_ON), NEGATIVE_OFF(
										Attribute.NEGATIVE_OFF), CONCEAL_ON(Attribute.CONCEAL_ON), CONCEAL_OFF(
												Attribute.CONCEAL_OFF), UNDERLINE_DOUBLE(
														Attribute.UNDERLINE_DOUBLE), UNDERLINE_OFF(
																Attribute.UNDERLINE_OFF),

		// Aliases
		BOLD(Attribute.INTENSITY_BOLD), FAINT(Attribute.INTENSITY_FAINT),;

		private final Enum<?> n;

		private final boolean background;

		private Code(final Enum<?> n, boolean background) {
			this.n = n;
			this.background = background;
		}

		private Code(final Enum<?> n) {
			this(n, false);
		}

		public boolean isColor() {
			return n instanceof Ansi.AnsiColor;
		}

		public Ansi.AnsiColor getColor() {
			return (Ansi.AnsiColor) n;
		}

		public boolean isAttribute() {
			return n instanceof Attribute;
		}

		public Attribute getAttribute() {
			return (Attribute) n;
		}

		public boolean isBackground() {
			return background;
		}
	}
}