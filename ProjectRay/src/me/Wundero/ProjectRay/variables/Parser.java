package me.Wundero.ProjectRay.variables;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Wundero.ProjectRay.framework.PlayerWrapper;

import com.google.common.collect.Lists;

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
public class Parser {
	private static Parser parser = new Parser();

	private Pattern pattern;

	private Parser() {
		setChars(new char[] { '%' }, new char[] { '%' }, '|', ':');
		createPattern();
	}

	public void createPattern() {
		String p1s = "[" + combine(startchars) + "]";
		String p2s = "[" + combine(endchars) + "]";
		String np1s = "[^" + p1s.substring(1, p1s.length() - 1)
				+ ra(p1s.substring(1, p1s.length() - 1), p2s.substring(1));
		String p = p1s + "(" + np1s + ")+" + p2s;
		pattern = Pattern.compile(p);
	}

	public static Parser get() {
		return parser == null ? parser = new Parser() : parser;
	}

	private char[] startchars = {}, endchars = {};
	private char data = '|', declarer = ':';
	private HashMap<Character, Character> unitonorm = new HashMap<>();
	private HashMap<Character, Character> normtouni = new HashMap<>();

	public void setChars(char[] s, char[] e, char data, char dec) {
		this.startchars = s;
		this.endchars = e;
		int uni = 20000;
		for (Character c : s) {
			if (!normtouni.containsKey(c)) {
				normtouni.put(c, Character.valueOf((char) uni));
				unitonorm.put(Character.valueOf((char) uni), c);
				uni++;
			}
		}
		for (Character c : e) {
			if (!normtouni.containsKey(c)) {
				normtouni.put(c, Character.valueOf((char) uni));
				unitonorm.put(Character.valueOf((char) uni), c);
				uni++;
			}
		}
		this.data = data;
		this.declarer = dec;
	}

	public boolean isStartChar(char c) {
		for (char s : startchars) {
			if (c == s) {
				return true;
			}
		}
		return false;
	}

	public boolean isEndChar(char c) {
		for (char s : endchars) {
			if (c == s) {
				return true;
			}
		}
		return false;
	}

	public String combine(char[] chars) {
		String out = "";
		for (char c : chars) {
			out += c;
		}
		return out;
	}

	public String fix(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		if (isStartChar(s.charAt(0))) {
			s = s.substring(1);
		}
		if (isEndChar(s.charAt(s.length() - 1))) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	private boolean hasOther(PlayerWrapper<?>[] o, int ind) {
		return o != null && (o.length > ind) && (o[ind] != null);
	}

	public String parseVariable(PlayerWrapper<?> player,
			PlayerWrapper<?>[] others, String name) {
		String nameo = name;
		name = name.toLowerCase();
		name = fix(name);
		if (name.startsWith("sender" + declarer) && hasOther(others, 0)) {// should
																			// be
																			// default
																			// case
			player = others[0];
		}
		if ((name.startsWith("recip" + declarer) || name.startsWith("recipient"
				+ declarer))
				&& hasOther(others, 1)) {
			player = others[1];
		}
		if (name.startsWith("viewer" + declarer) && hasOther(others, 2)) {// secondary
																			// default
																			// case
			player = others[2];
		}
		if (name.contains("¦")) {
			name = name.replace("¦", "|");
		}
		if (!name.contains("" + data)) {
			Variable v = Store.get().get(name);
			if (v == null) {
				return nameo;
			}
			return v.parse(player, null);
		}

		String[] spl = name.split(Pattern.quote("" + data));
		name = spl[0];
		String[] data = new String[spl.length - 1];
		int i = 0;
		boolean first = true;
		for (String s : spl) {
			if (first) {
				first = false;
				continue;
			}
			data[i++] = s;
		}

		Variable v = Store.get().get(name);
		if (v == null) {
			return nameo;
		}
		return v.parse(player, data);
	}

	private String ra(String c1, String c2) {
		for (Character c : c1.toCharArray()) {
			c2 = c2.replace(c + "", "");
		}
		return c2;
	}

	private String f(String s) {
		if (s == null || s.isEmpty()) {
			return "";
		}
		if (fix(s) == s) {
			return s;
		}
		return normtouni.get(s.charAt(0)) + "" + s.substring(1, s.length() - 1)
				+ "" + normtouni.get(s.charAt(s.length() - 1));
	}

	public String parseIteration(PlayerWrapper<?> player,
			PlayerWrapper<?>[] others, String s, int iterations) {
		String out = s;
		for (int i = 0; i < iterations; i++) {
			out = parse(player, others, out);
		}
		return out;
	}

	public List<String> parseListIteration(Iterable<String> list,
			PlayerWrapper<?> player, PlayerWrapper<?>[] others, int iterations) {
		List<String> l = Lists.newArrayList();
		for (String s : list) {
			l.add(parseIteration(player, others, s, iterations));
		}
		return l;
	}

	public List<String> parseListUntilDone(Iterable<String> list,
			PlayerWrapper<?> player, PlayerWrapper<?>[] others) {
		List<String> l = Lists.newArrayList();
		for (String s : list) {
			l.add(parseUntilDone(player, others, s));
		}
		return l;
	}

	public String parseUntilDone(PlayerWrapper<?> player,
			PlayerWrapper<?>[] others, String s) {
		String out = s;
		String parsed = s;
		while (out != (parsed = parse(player, others, out))) {
			out = parsed;
		}
		return out;
	}

	public List<String> parseList(Iterable<String> list,
			PlayerWrapper<?> player, PlayerWrapper<?>[] others) {
		List<String> l = Lists.newArrayList();
		for (String s : list) {
			l.add(parse(player, others, s));
		}
		return l;
	}

	public String parse(PlayerWrapper<?> player, PlayerWrapper<?>[] others,
			String st) {
		String s = st;
		Matcher ma = pattern.matcher(s);
		while (ma.reset(s).find()) {
			s = s.replace("" + ma.group(0),
					f(parseVariable(player, others, ma.group(0))));
		}
		return x(s);
	}

	private String x(String s) {
		for (Character c : unitonorm.keySet()) {
			s = s.replace(c, unitonorm.get(c));
		}
		return s;
	}

}
