package me.Wundero.Ray.framework;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.config.Rootable;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.format.FormatCollection;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.trigger.Trigger;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

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
@ConfigSerializable
public class Group implements Rootable {

	public static TypeToken<Group> type = TypeToken.of(Group.class);

	@Setting("formats")
	private Map<String, Format> all = Utils.sm();
	private Map<FormatContext, FormatCollection> formats = Utils.sm();
	@Setting
	private List<String> parents = Utils.sl();
	@Setting
	private int priority = 0;
	@Setting
	private String permission;
	@Setting
	private Map<String, Trigger> triggers = Utils.sm();
	private String name;
	@Nullable
	private ConfigurationNode config;

	/**
	 * Add a format to the group.
	 */
	public void addFormat(Format format) {
		all.put(format.getName(), format);
		applyAll();
	}

	private void applyAll() {
		all.forEach((s, f) -> {
			FormatContext type = f.getContext();
			FormatCollection fc = formats.get(type);
			if (fc == null) {
				fc = new FormatCollection();
			}
			if (fc.contains(f)) {
				return;
			}
			fc.add(f);
			formats.put(type, fc);
		});
	}

	/**
	 * @return all formats on all worlds.
	 */
	public FormatCollection getAllFormats() {
		List<Format> out = Utils.al();
		all.forEach((s, f) -> out.add(f));
		return new FormatCollection(out);
	}

	/**
	 * @param recurseTimes
	 *            number of times to recurse.
	 * 
	 * @return all formats on all worlds, for this and parents.
	 */
	public FormatCollection getAllFormats(int recurseTimes) {
		List<Format> out2 = Utils.al();
		out2.addAll(getAllFormats().get());
		List<Group> groups = getParentsGroups();
		for (Group g : groups) {
			out2.addAll(recurseTimes > 0 || recurseTimes < 16 ? g.getAllFormats(recurseTimes - 1).get()
					: g.getAllFormats().get());
		}
		return new FormatCollection(out2);
	}

	/**
	 * Return a list of the groups this group directly inherits from. Safeguards
	 * against infinite parent loops are in place.
	 */
	public List<Group> getParentsGroups() {
		List<Group> groups = Utils.al(Ray.get().getGroups().getGroups(), true);
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
			List<Group> groups = Utils.al(Ray.get().getGroups().getGroups(), true);
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
		return Utils.wrap2(permission, perm -> perm.isPresent() && !perm.get().isEmpty());
	}

	/**
	 * Set the permission for this group.
	 */
	public void setPermission(String permission) {
		this.permission = permission;
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

	@Override
	public void applyRoot(String name, ConfigurationNode root) {
		setName(name);
		setConfig(root);
		for (Map.Entry<String, Format> e : all.entrySet()) {
			e.getValue().applyRoot(name, root.getNode("formats").getNode(e.getKey()));
		}
		applyAll();
		this.triggers.forEach((s, t) -> t.setGroup(this));
	}

	/**
	 * @return the config
	 */
	public ConfigurationNode getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}
}
