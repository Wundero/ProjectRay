package me.Wundero.ProjectRay.framework;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

public abstract class DataHolder {

	protected HashMap<String, Object> data = Maps.newHashMap();

	protected UUID uuid;

	public synchronized Object getData(String key) {
		return data.get(key);
	}

	public synchronized boolean hasData(String key) {
		return data.containsKey(key);
	}

	public synchronized Object removeData(String key) {
		return data.remove(key);
	}

	public synchronized void putData(String key, Object value) {
		data.put(key, value);
	}

	public synchronized void clearData() {
		data.clear();
	}

	public synchronized UUID getUUID() {
		return uuid;
	}

	public synchronized void setUUID(UUID u) {
		this.uuid = u;
	}

}
