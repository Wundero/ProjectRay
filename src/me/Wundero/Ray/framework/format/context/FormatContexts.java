package me.Wundero.Ray.framework.format.context;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import me.Wundero.Ray.utils.Utils;

public class FormatContexts {

	public static FormatContext CHAT = new FormatContext("chat", new String[] { "c" });
	public static FormatContext MESSAGE_SEND = new FormatContext("send message",
			new String[] { "sm", "smsg", "sendmsg" });
	public static FormatContext MESSAGE_RECEIVE = new FormatContext("receive message",
			new String[] { "rm", "rmsg", "receivemsg" });
	public static FormatContext MESSAGE_SPY = new FormatContext("spy", new String[] { "message spy" });
	public static FormatContext JOIN = new FormatContext("join", new String[] { "j" });
	public static FormatContext LEAVE = new FormatContext("leave", new String[] { "l" });
	public static FormatContext CUSTOM = new FormatContext("custom");
	public static FormatContext WELCOME = new FormatContext("welcome", new String[] { "w" });
	public static FormatContext MOTD = new FormatContext("motd");
	public static FormatContext TABLIST_ENTRY = new FormatContext("tablist", new String[] { "list", "t", "tab" }, true);
	public static FormatContext DEFAULT = new FormatContext("default");
	public static FormatContext ACHIEVEMENT = new FormatContext("achievement", new String[] { "ach" });
	public static FormatContext KICK = new FormatContext("kick", new String[] { "k" });
	public static FormatContext TABLIST_HEADER = new FormatContext("header", new String[] { "h" }, true);
	public static FormatContext TABLIST_FOOTER = new FormatContext("footer", new String[] { "f" }, true);
	public static FormatContext ANNOUNCEMENT = new FormatContext("announcement", new String[] { "a" });

	private static Map<String, FormatContext> lookup = Utils.sm();

	static {
		for (Field f : FormatContexts.class.getDeclaredFields()) {
			try {
				FormatContext type = (FormatContext) f.get(null);// class cast
																	// caught
				// and ignored
				rft(type);
			} catch (Exception e) {
			}
		}
	}

	public static FormatContext fromString(String value) {
		return FormatContext.fromString(value);
	}

	private static void rft(FormatContext type) {
		lookup.put(type.getName(), type);
		for (String a : type.getAliases()) {
			lookup.put(a, type);
		}
	}

	public static List<FormatContext> values() {
		return Utils.sl(lookup.values());
	}

	public static boolean put(FormatContext type) {
		if (get(type.getName()) == null) {
			for (String a : type.getAliases()) {
				if (get(a) != null) {
					return false;
				}
			}
			rft(type);
			return true;
		}
		return false;
	}

	public static FormatContext get(String name) {
		return lookup.get(name.toLowerCase().trim());
	}

}
