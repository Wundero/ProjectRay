package me.Wundero.ProjectRay.variables;

import java.util.HashMap;

public class Store {

	private HashMap<String, Variable> vars = new HashMap<>();

	private static Store store;

	private Store() {
		vars = new HashMap<>();
	}

	public void clear() {
		for (String s : vars.keySet()) {
			remove(s);
		}
		vars.clear();
	}
	
	public void clear(boolean unsafe) {
		if(!unsafe) {
			clear();
		} else {
			vars.clear();
		}
	}

	public static Store get() {
		if (store == null) {
			store = new Store();
		}
		return store;
	}

	public Variable get(String id) {
		return vars.get(Parser.get().fix(id.toLowerCase()));
	}

	public void remove(Variable v) {
		remove(v.getName());
	}

	public void remove(String id) {
		this.vars.remove(Parser.get().fix(id.toLowerCase())).unregister();
	}

	public void add(Variable v) {
		String id = v.getName();
		id = id.toLowerCase();
		vars.put(id, v);
		v.register();
	}
}
