package me.Wundero.ProjectRay.framework;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.framework.format.Format;
import me.Wundero.ProjectRay.framework.format.FormatType;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

//stores a bunch of formats and is chosen based on permission
public class Group {
	private Map<FormatType, List<Format>> formats = Utils.sm();
	private List<String> parents = Utils.sl();
	private String world;
	private int priority;
	private Optional<String> permission;
	private ConfigurationNode config;
	private String name;
	private boolean global = false;

	public Group(String world, ConfigurationNode config, boolean global) {
		this.setGlobal(global);
		this.setWorld(world);
		this.setConfig(config);
		load();
	}

	public void addFormat(Format format) {
		FormatType type = format.getType();
		List<Format> f = formats.get(type);
		if (f == null) {
			f = Utils.sl();
		}
		f.add(format);
		formats.put(type, f);
	}

	public synchronized void load() {
		this.setName(config.getKey().toString());
		this.setPermission(config.getNode("permission").getString());
		this.setPriority(config.getNode("priority").getInt(0));
		try {
			this.setParents(config.getNode("parents").getList(TypeToken.of(String.class)));
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		loadFormats();
	}

	private synchronized void lf(ConfigurationNode nod) {
		ConfigurationNode nod2;
		if (nod.getNode("formats").isVirtual()) {
			nod2 = nod;
		} else {
			nod2 = nod.getNode("formats");
		}
		for (ConfigurationNode node : nod2.getChildrenMap().values()) {
			Format f = Format.create(node);
			FormatType type = f.getType();
			if (!formats.containsKey(type)) {
				formats.put(type, Utils.sl(f));
			} else {
				List<Format> list = formats.get(type);
				list.add(f);
				formats.put(type, list);
			}
		}
	}

	private synchronized void loadFormats() {
		lf(config);
	}

	public Format getFormat(FormatType type, int index) {
		if (getFormats(type) == null || getFormats(type).isEmpty()) {
			return null;
		}
		if (getFormats(type).size() <= index) {
			return null;
		}
		return getFormats(type).get(index);
	}

	public Format getFormat(FormatType type) {
		return getFormat(type, 0);
	}

	public Format getFormat(FormatType type, boolean random) {
		if (random) {
			return getRandomFormat(type);
		}
		return getFormat(type, 0);
	}

	public Format getRandomFormat(FormatType type) {
		List<Format> fmats = getFormats(type);
		return fmats.get(new Random().nextInt(fmats.size()));
	}

	public Format getFormat(FormatType type, String name) {
		for (Format f : getFormats(type)) {
			if (f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}

	public List<Format> getAllFormats() {
		List<Format> out = Utils.sl();
		for (List<Format> f : formats.values()) {
			out.addAll(f);
		}
		return out;
	}

	public List<Format> getAllFormats(boolean inherits, int recurseTimes) {
		List<Format> out2 = Utils.sl();
		out2.addAll(getAllFormats());
		List<Group> groups = getParentsGroups();
		for (Group g : groups) {
			out2.addAll(recurseTimes > 0 ? g.getAllFormats(inherits, recurseTimes - 1) : g.getAllFormats());
		}
		return out2;
	}

	public List<Group> getParentsGroups() {
		List<Group> groups = Utils.sl(Ray.get().getGroups().getGroups(world).values());
		List<Group> torem = Utils.sl();
		for (Group g : groups) {
			if (!parents.contains(g.name)) {
				torem.add(g);
			}
		}
		for (Group g : torem) {
			groups.remove(g);
		}
		groups.sort(new Comparator<Group>() {
			@Override
			public int compare(Group o1, Group o2) {
				return o1.priority - o2.priority;
			}
		});
		return groups;
	}

	public List<Format> getFormats(FormatType type) {
		if (formats.get(type) == null || formats.get(type).isEmpty()) {
			List<Group> groups = Utils.sl(Ray.get().getGroups().getGroups(world).values());
			List<Group> torem = Utils.sl();
			for (Group g : groups) {
				if (!parents.contains(g.name)) {
					torem.add(g);
				}
			}
			for (Group g : torem) {
				groups.remove(g);
			}
			groups.sort(new Comparator<Group>() {
				@Override
				public int compare(Group o1, Group o2) {
					return o1.priority - o2.priority;
				}
			});
			List<Format> formats;
			for (Group g : groups) {
				formats = g.getFormats(type);
				if (!(formats == null || formats.isEmpty())) {
					return formats;
				}
			}
			return Utils.sl();
		}
		return formats.get(type);
	}

	public ConfigurationNode getConfig() {
		return config;
	}

	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

	public List<String> getParents() {
		return parents;
	}

	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Optional<String> getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = Optional.ofNullable(permission);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}
}
