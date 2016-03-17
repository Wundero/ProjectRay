package me.Wundero.ProjectRay.framework.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;

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

	public ConfigSection createSection(String path) {
		if (!path.contains(".")) {
			ConfigSection c = new ConfigSection(path, this);
			set(path, c);
			return c;
		} else {
			String p = "";
			String[] k = path.split(".");
			p = k[k.length - 1];
			ConfigSection c = new ConfigSection(p, this);
			set(path, c);
			return c;
		}
	}

	public ConfigSection createSection(String path, Map<String, Object> values) {
		ConfigSection s = createSection(path);
		s.data = values;
		return s;
	}

	public boolean getBoolean(String path) {
		if (!isBoolean(path)) {
			return false;
		}
		return (Boolean) get(path, false);
	}

	public boolean getBoolean(String path, boolean def) {
		if (!isBoolean(path)) {
			return def;
		}
		return (Boolean) get(path, def);
	}

	@SuppressWarnings("unchecked")
	public List<Boolean> getBooleanList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Boolean) {
			return (List<Boolean>) l;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Byte> getByteList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Byte) {
			return (List<Byte>) l;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Character> getCharacterList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Character) {
			return (List<Character>) l;
		}
		return null;
	}

	public Color getColor(String path) {
		if (!isColor(path)) {
			return null;
		}
		return (Color) get(path);
	}

	public Color getColor(String path, Color def) {
		Color c = getColor(path);
		return c == null ? def : c;
	}

	public ConfigSection getConfigSection(String path, boolean force) {
		if (!contains(path) && force) {
			return createSection(path);
		}
		if (!isConfigSection(path)) {
			return null;
		}
		return (ConfigSection) get(path);
	}

	public String getCurrentPath() {
		if (parent == null) {
			return getName();
		}
		return parent.getCurrentPath() + "." + getName();
	}

	public double getDouble(String path) {
		if (!isDouble(path)) {
			return 0;
		}
		return (Double) get(path, 0);
	}

	public double getDouble(String path, double def) {
		if (!isDouble(path)) {
			return def;
		}
		return (Double) get(path, def);
	}

	@SuppressWarnings("unchecked")
	public List<Double> getDoubleList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Double) {
			return (List<Double>) l;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Float> getFloatList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Float) {
			return (List<Float>) l;
		}
		return null;
	}

	public int getInt(String path) {
		if (!isInt(path)) {
			return 0;
		}
		return (Integer) get(path, 0);
	}

	public int getInt(String path, int def) {
		if (!isInt(path)) {
			return def;
		}
		return (Integer) get(path, def);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIntegerList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Integer) {
			return (List<Integer>) l;
		}
		return null;
	}

	public List<?> getList(String path) {
		if (!isList(path)) {
			return null;
		}
		return (List<?>) get(path);
	}

	public List<?> getList(String path, List<?> def) {
		List<?> l = getList(path);
		return l == null ? def : l;
	}

	public long getLong(String path) {
		if (!isLong(path)) {
			return 0;
		}
		return (Long) get(path, 0);
	}

	public long getLong(String path, long def) {
		if (!isLong(path)) {
			return def;
		}
		return (Long) get(path, def);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getLongList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Long) {
			return (List<Long>) l;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Map<?, ?>> getMapList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Map<?, ?>) {
			return (List<Map<?, ?>>) l;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public List<Short> getShortList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof Short) {
			return (List<Short>) l;
		}
		return null;
	}

	public String getString(String path) {
		if (!isString(path)) {
			return null;
		}
		return (String) get(path);
	}

	public String getString(String path, String def) {
		String s = getString(path);
		return s == null ? def : s;
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String path) {
		if (!isList(path)) {
			return null;
		}
		List<?> l = getList(path);
		if (l.isEmpty()) {
			return Lists.newArrayList();
		}
		if (l.get(0) instanceof String) {
			return (List<String>) l;
		}
		return null;
	}

	public Map<String, Object> getValues(boolean deep) {
		Map<String, Object> v = Maps.newHashMap(data);
		if (deep) {
			for (Object o : v.values()) {
				if (o instanceof ConfigSection) {
					ConfigSection c = (ConfigSection) o;
					Map<String, Object> v2 = c.getValues(deep);
					String s = c.getName();
					for (String s2 : v2.keySet()) {
						v.put(s + "." + s2, v2.get(s2));
					}
				}
			}
		}
		return v;
	}

	public boolean isBoolean(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Boolean;
	}

	public boolean isColor(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Color;
	}

	public boolean isConfigSection(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof ConfigSection;
	}

	public boolean isDouble(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Double;
	}

	public boolean isInt(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Integer;
	}

	public boolean isList(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof List;
	}

	public boolean isLong(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Long;
	}

	public boolean isSet(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof Set;
	}

	public boolean isString(String path) {
		if (!contains(path)) {
			return false;
		}
		return get(path) instanceof String;
	}

}
