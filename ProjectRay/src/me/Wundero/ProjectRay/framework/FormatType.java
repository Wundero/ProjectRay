package me.Wundero.ProjectRay.framework;

import me.Wundero.ProjectRay.variables.Variable;

public enum FormatType {

	CHAT("chat", new String[] { "c" }, new Variable[] {}), MESSAGE("message",
			new String[] { "m", "msg" }, new Variable[] {}), DEATH("death",
			new String[] { "d" }, new Variable[] {}), JOIN(""), LEAVE(""), CUSTOM(
			""), WELCOME(""), MAIL(""), MOTD(""), ANNOUNCEMENT(""), DEFAULT("");

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
