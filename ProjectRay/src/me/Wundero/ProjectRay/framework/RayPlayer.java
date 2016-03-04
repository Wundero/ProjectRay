package me.Wundero.ProjectRay.framework;

import org.bukkit.OfflinePlayer;

public class RayPlayer extends DataHolder {
	private OfflinePlayer player;
	private String lastName;

	public String getLastName() {
		return lastName;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	// Not worth it to implement yet
	/*
	 * public void cacheVariableResult(String var, String[] data, Object result)
	 * {
	 * 
	 * this.putData(jvd(var, data), result); }
	 * 
	 * private String jvd(String var, String[] data) { List<String> list =
	 * Lists.newArrayList("variable: " + var); for (String s : data) {
	 * list.add(s); } return Utils.join("|", list); }
	 * 
	 * public boolean hasVariableCache(String var, String[] data) { return
	 * this.hasData(jvd(var, data)); }
	 * 
	 * public Object getVariableCache(String var, String[] data) { if
	 * (!hasVariableCache(var, data)) { return null; } return
	 * this.getData(jvd(var, data)); }
	 */
}
