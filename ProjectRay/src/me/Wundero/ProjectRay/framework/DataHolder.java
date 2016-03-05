package me.Wundero.ProjectRay.framework;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

public abstract class DataHolder {

	protected HashMap<String, Object> data = Maps.newHashMap();

	protected UUID uuid;

	@SuppressWarnings("unchecked")
	public synchronized <T> T getData(String key) {
		return (T) data.get(key);
	}

	public synchronized boolean hasData(String key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T removeData(String key) {
		return (T) data.remove(key);
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
