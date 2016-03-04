package me.Wundero.ProjectRay.framework;

import me.Wundero.ProjectRay.variables.Variable;

public enum FormatType {

	CHAT("chat", new String[] { "c" }, new Variable[] {}), MESSAGE("message",
			new String[] { "m", "msg" }, new Variable[] {}), DEATH("death",
			new String[] { "d" }, new Variable[] {}), JOIN(""), LEAVE(""), TAB_NAME(
			""), CUSTOM(""), WELCOME(""), MAIL(""), MOTD("");

	// Perhaps actionbar/scoreboard/bossbar/title/tabheaderfooter

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
		this.name = name;
	}
}
