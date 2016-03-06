package me.Wundero.ProjectRay.framework;

import me.Wundero.ProjectRay.variables.Variable;

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
public enum FormatType {

	CHAT("chat", new String[] { "c" }, new Variable[] {}), MESSAGE("message",
			new String[] { "m", "msg" }, new Variable[] {}), DEATH("death",
			new String[] { "d" }, new Variable[] {}), JOIN("join",
			new String[] { "j" }, new Variable[] {}), LEAVE("leave",
			new String[] { "l" }, new Variable[] {}), CUSTOM("custom"), WELCOME(
			"welcome", new String[] { "w" }, new Variable[] {}), MAIL("mail",
			new String[] {}, new Variable[] {}), MOTD("motd", new String[] {},
			new Variable[] {}), ANNOUNCEMENT("announcement",
			new String[] { "a" }, new Variable[] {}), DEFAULT("default");

	// Perhaps actionbar/scoreboard/bossbar/title/tab (name header and
	// footer)/item names/block gui names/other random text stuff
	// all these could be animated
	// also perhaps serverlist message (not anim)

	private String[] aliases;
	private Variable[] customVariables;
	private String name;

	FormatType(String name) {
		this.setName(name);
	}

	FormatType(String name, String[] aliases) {
		this.setName(name);
		this.setAliases(aliases);
	}

	FormatType(String name, String[] aliases, Variable[] customVariables) {
		this.setName(name);
		this.setAliases(aliases);
		this.setCustomVariables(customVariables);
	}

	public Variable[] getCustomVariables() {
		return customVariables;
	}

	public void setCustomVariables(Variable[] customVariables) {
		this.customVariables = customVariables;
	}

	public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public static FormatType fromString(String s) {
		s = s.trim();
		for (FormatType type : values()) {
			if (type.name.equalsIgnoreCase(s)) {
				return type;
			}
		}
		for (FormatType type : values()) {
			for (String st : type.aliases) {
				if (st.equalsIgnoreCase(s)) {
					return type;
				}
			}
		}
		return DEFAULT;
	}
}
