package me.Wundero.ProjectRay.framework.config;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

//Based off of Bukkit's ConfigurationSection but made to abstract from Bukkit and support Sponge
public class ConfigSection {

	protected Map<String, Object> data = Maps.newHashMap();
	private String name;
	private ConfigSection parent;

	public ConfigSection(String name, ConfigSection parent) {
		this.setName(name);
		this.setParent(parent);
	}

	public Object get(String path) {
		if (path.contains(".")) {
			String p = path.split(".")[0];
			if (!(data.get(p) instanceof ConfigSection)) {
				return null;
			}
			ConfigSection sect = (ConfigSection) data.get(p);
			if (sect != null) {
				return sect.get(path.substring(p.length() + 1));
			} else {
				return null;
			}
		}
		return data.get(path);
	}

	public Object get(String path, Object def) {
		Object o = get(path);
		return o == null ? def : o;
	}

	public boolean contains(String path) {
		return get(path) != null;
	}

	public synchronized boolean set(String path, Object data) {
		if (path.contains(".")) {
			String p = path.split(".")[0];
			if (get(p) != null) {
				if (!(get(p) instanceof ConfigSection)) {
					return false;
				}
				return ((ConfigSection) get(p)).set(
						path.substring(p.length() + 1), data);
			} else {
				ConfigSection n = new ConfigSection(p, this);
				n.set(path.substring(p.length() + 1), data);
				this.data.put(p, n);
			}
		} else {
			this.data.put(path, data);
		}
		return true;
	}

	public List<String> getKeys(boolean deep) {
		List<String> k = Lists.newArrayList(data.keySet());
		if (deep) {
			for (Object o : data.values()) {
				if (o instanceof ConfigSection) {
					k.addAll(((ConfigSection) o).getKeys(deep));
				}
			}
		}
		return k;
	}

	public ConfigSection getRoot() {
		if (parent == null) {
			return this;
		}
		return parent.getRoot();
	}

	public ConfigSection getParent() {
		return parent;
	}

	private void setParent(ConfigSection parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
}
