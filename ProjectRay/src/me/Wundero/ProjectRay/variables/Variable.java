package me.Wundero.ProjectRay.variables;

import org.bukkit.OfflinePlayer;

public abstract class Variable {

	public Variable(String s) {
		setName(s);
	}

	private String name;

	public abstract String parse(OfflinePlayer player, String[] data);

	public final String getName() {
		return name;
	}

	public void register() {
		
	}
	public void unregister() {
		
	}
	
	public final void setName(String name) {
		this.name = name.toLowerCase();
		fix();
	}

	private final void fix() {
		name = Parser.get().fix(name);
	}
}
