package me.Wundero.Ray.translation;

import java.util.Locale;
import java.util.Map;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.translation.locale.Locales;

import me.Wundero.Ray.utils.TextUtils;

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

/**
 * Class to translate messages for a player.
 */
public class Translator {

	private Locale l;
	private I18n messages;

	/**
	 * Create a new, source-specific translator.
	 */
	public Translator(CommandSource p) {
		try {
			l = p.getLocale();
		} catch (Exception e) {
			l = Locales.DEFAULT;
		}
		messages = I18n.get(l, true);
	}

	/**
	 * Parse the string into a text template, and then replace variables.
	 */
	public Text t2(String key, Map<String, Object> args) {
		String m = messages.translate(key);
		TextTemplate template = TextUtils.parse(m, true);
		for (String k : template.getArguments().keySet()) {
			if (!args.containsKey(k)) {
				args.put(k, "");
			}
		}
		return template.apply(args).build();
	}

	/**
	 * Parse the string using message formatters, then parse colors.
	 */
	public Text t(String key, Object... args) {
		if (messages == null) {
			return Text.EMPTY;
		}
		return messages.t(key, args);
	}

	/**
	 * Parse the string using message formatters.
	 */
	public String tl(String key, Object... args) {
		if (messages == null) {
			return key;
		}
		return messages.tl(key, args);
	}

}
