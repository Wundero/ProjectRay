package me.Wundero.Ray.framework.format.context;

import java.util.regex.Pattern;

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
 * Represents a context for a format. A context is how the plugin decides when
 * to send a format.
 */
public class FormatContext {

	private static Pattern namepat = Pattern.compile("[a-zA-Z]+[_\\-\\. ]*[0-9]+", Pattern.CASE_INSENSITIVE);
	private static Pattern altpat = Pattern.compile("[_\\-\\. ]*[0-9]+", Pattern.CASE_INSENSITIVE);

	private String[] aliases;
	private String name;

	/**
	 * Create a new context.
	 */
	public FormatContext(String name) {
		this.setName(name);
		this.setAliases(new String[] {});
	}

	/**
	 * Create a new context.
	 */
	public FormatContext(String name, String[] aliases) {
		this.setName(name);
		this.setAliases(aliases);
	}

	/**
	 * @return the aliases for the name.
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Set the aliases.
	 */
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 */
	public void setName(String name) {
		this.name = name.trim();
	}

	/**
	 * Get a context from a string.
	 */
	public static FormatContext fromString(String s) {
		if (s == null) {
			return FormatContexts.DEFAULT;
		}
		s = s.trim().toUpperCase().replace("_", " ");
		if (namepat.matcher(s).matches()) {
			s = altpat.matcher(s).replaceAll("");
		}
		for (FormatContext type : FormatContexts.values()) {
			if (type.name.equalsIgnoreCase(s) || type.getName().equalsIgnoreCase(s)) {
				return type;
			}
		}
		for (FormatContext type : FormatContexts.values()) {
			for (String st : type.aliases) {
				if (st.equalsIgnoreCase(s)) {
					return type;
				}
			}
		}
		return FormatContexts.DEFAULT;
	}
}
