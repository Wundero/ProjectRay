package me.Wundero.ProjectRay.framework.iface;

public interface Cacheable {
	void clear();

	void cache(String[] params, String result);
}
