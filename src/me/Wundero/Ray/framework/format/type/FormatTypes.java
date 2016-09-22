package me.Wundero.ProjectRay.framework.format.type;
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

import me.Wundero.ProjectRay.utils.Utils;

public class FormatTypes {

	public static FormatType CHAT = new FormatType("chat", new String[] { "c" });
	public static FormatType MESSAGE_SEND = new FormatType("send message", new String[] { "sm", "smsg", "sendmsg" });
	public static FormatType MESSAGE_RECEIVE = new FormatType("receive message",
			new String[] { "rm", "rmsg", "receivemsg" });
	public static FormatType MESSAGE_SPY = new FormatType("spy", new String[] { "message spy" });
	public static FormatType JOIN = new FormatType("join", new String[] { "j" });
	public static FormatType LEAVE = new FormatType("leave", new String[] { "l" });
	public static FormatType CUSTOM = new FormatType("custom");
	public static FormatType WELCOME = new FormatType("welcome", new String[] { "w" });
	public static FormatType MOTD = new FormatType("motd");
	public static FormatType TABLIST_ENTRY = new FormatType("tablist", new String[] { "list", "t", "tab" }, true);
	public static FormatType DEFAULT = new FormatType("default");
	public static FormatType ACHIEVEMENT = new FormatType("achievement", new String[] { "ach" });
	public static FormatType KICK = new FormatType("kick", new String[] { "k" });
	public static FormatType TABLIST_HEADER = new FormatType("header", new String[] { "h" }, true);
	public static FormatType TABLIST_FOOTER = new FormatType("footer", new String[] { "f" }, true);
	public static FormatType ANNOUNCEMENT = new FormatType("announcement", new String[] { "a" });

	private static Map<String, FormatType> lookup = Utils.sm();

	static {
		for (Field f : FormatTypes.class.getDeclaredFields()) {
			try {
				FormatType type = (FormatType) f.get(null);// class cast caught
															// and ignored
				rft(type);
			} catch (Exception e) {
			}
		}
	}

	public static FormatType fromString(String value) {
		return FormatType.fromString(value);
	}

	private static void rft(FormatType type) {
		lookup.put(type.getName(), type);
		for (String a : type.getAliases()) {
			lookup.put(a, type);
		}
	}

	public static List<FormatType> values() {
		return Utils.sl(lookup.values());
	}

	public static boolean put(FormatType type) {
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

	public static FormatType get(String name) {
		return lookup.get(name.toLowerCase().trim());
	}

}
