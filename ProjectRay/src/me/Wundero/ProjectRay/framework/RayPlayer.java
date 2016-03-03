package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.utils.Utils;

import org.bukkit.OfflinePlayer;

import com.google.common.collect.Lists;

public class RayPlayer extends DataHolder {
	private OfflinePlayer player;
	private String lastName;

	public String getLastName() {
		return lastName;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void cacheVariableResult(String var, String[] data, Object result) {

		this.putData(jvd(var, data), result);
	}

	private String jvd(String var, String[] data) {
		List<String> list = Lists.newArrayList("variable: " + var);
		for (String s : data) {
			list.add(s);
		}
		return Utils.join("|", list);
	}

	public boolean hasVariableCache(String var, String[] data) {
		return this.hasData(jvd(var, data));
	}

	public Object getVariableCache(String var, String[] data) {
		if (!hasVariableCache(var, data)) {
			return null;
		}
		return this.getData(jvd(var, data));
	}
}
