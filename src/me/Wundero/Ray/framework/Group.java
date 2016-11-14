package me.Wundero.Ray.framework;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.format.FormatCollection;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.utils.Utils;
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

/**
 * Represents an object which holds formats and data in memory. Each group has a
 * map that stores formats against contexts. It also holds a list of groups it
 * inherits from, the world on which it is active, the priority (higher number
 * means it is chosen over other groups), a permission, and additional info that
 * determines internal functionality.
 */
public class Group {
	private Map<FormatContext, FormatCollection> formats = Utils.sm();
	private List<String> parents = Utils.sl();
	private String world;
	private int priority;
	private Optional<String> permission;
	private ConfigurationNode config;
	private String name;
	private boolean global = false;

	/**
	 * Create a new group.
	 */
	public Group(String world, ConfigurationNode config, boolean global) {
		this.setGlobal(global);
		this.setWorld(world);
		this.setConfig(config);
		load();
	}

	/**
	 * Add a format to the group.
	 */
	public void addFormat(Format format) {
		FormatContext type = format.getContext();
		FormatCollection f = formats.get(type);
		if (f == null) {
			f = new FormatCollection();
		}
		f.add(format);
		formats.put(type, f);
	}

	/**
	 * Load the group from the config node.
	 */
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
			FormatContext type = f.getContext();
			if (!formats.containsKey(type)) {
				formats.put(type, new FormatCollection(Utils.sl(f)));
			} else {
				FormatCollection list = formats.get(type);
				list.add(f);
				formats.put(type, list);
			}
		}
	}

	private synchronized void loadFormats() {
		lf(config);
	}

	/**
	 * @return all formats on all worlds.
	 */
	public FormatCollection getAllFormats() {
		List<Format> out = Utils.al();
		for (FormatCollection f : formats.values()) {
			out.addAll(f.get());
		}
		return new FormatCollection(out);
	}

	/**
	 * @return all formats on all worlds, for this and parents. RecurseTimes is
	 *         a safeguard against stack overflow.
	 */
	public FormatCollection getAllFormats(boolean inherits, int recurseTimes) {
		List<Format> out2 = Utils.al();
		out2.addAll(getAllFormats().get());
		List<Group> groups = getParentsGroups();
		for (Group g : groups) {
			out2.addAll(recurseTimes > 0 ? g.getAllFormats(inherits, recurseTimes - 1).get() : g.getAllFormats().get());
		}
		return new FormatCollection(out2);
	}

	/**
	 * Return a list of the groups this group directly inherits from. Safeguards
	 * against infinite parent loops are in place.
	 */
	public List<Group> getParentsGroups() {
		List<Group> groups = Utils.al(Ray.get().getGroups().getGroups(world).values(), true);
		List<Group> torem = Utils.al();
		for (Group g : groups) {
			if (!parents.contains(g.name)) {
				torem.add(g);
			}
			if (g.parents.contains(this.name) && parents.contains(g.name)) {
				g.parents.remove(this.name);
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

	/**
	 * Get all formats that match a name.
	 */
	public FormatCollection getFormats(FormatContext type, String name) {
		FormatCollection col = getFormats(type);
		List<Format> out = Utils.al();
		for (Format f : col.get()) {
			if (f.getName().equals(name)) {
				out.add(f);
			}
		}
		return new FormatCollection(out);
	}

	/**
	 * Get all formats from a context. Checks against parents.
	 */
	public FormatCollection getFormats(FormatContext type) {
		if (formats.get(type) == null || formats.get(type).isEmpty()) {
			List<Group> groups = Utils.al(Ray.get().getGroups().getGroups(world).values(), true);
			List<Group> torem = Utils.al();
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
			FormatCollection formats;
			for (Group g : groups) {
				formats = g.getFormats(type);
				if (!(formats == null || formats.isEmpty())) {
					return formats;
				}
			}
			return new FormatCollection();
		}
		return formats.get(type);
	}

	/**
	 * Return the node for this group.
	 */
	public ConfigurationNode getConfig() {
		return config;
	}

	/**
	 * Set the node for this group.
	 */
	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

	/**
	 * Get the names of all parent groups.
	 */
	public List<String> getParents() {
		return parents;
	}

	/**
	 * Set the names of parents.
	 */
	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	/**
	 * Get the world.
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * Set the world.
	 */
	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * Get the priority of the group.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set the priority.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Get the permission, if it exists, for this group.
	 */
	public Optional<String> getPermission() {
		return permission;
	}

	/**
	 * Set the permission for this group.
	 */
	public void setPermission(String permission) {
		this.permission = Utils.wrap2(permission, perm -> perm.isPresent() && !perm.get().isEmpty());
	}

	/**
	 * Return the name of the group.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the group.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return whether the group is on all worlds.
	 */
	public boolean isGlobal() {
		return global;
	}

	/**
	 * set whether the group is on all worlds.
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
}
