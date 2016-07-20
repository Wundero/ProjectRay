package me.Wundero.ProjectRay.framework;
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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class Groups {
	private Map<String, Map<String, Group>> groups = Maps.newHashMap();

	public Groups(ConfigurationNode node) {
		Ray.get().getPlugin().getLogger().info("1" + node.getKey().toString());
		for (ConfigurationNode child : node.getChildrenMap().values()) {
			Ray.get().getPlugin().getLogger().info("2" + child.getKey().toString());
			boolean global = false;
			String world = child.getKey().toString();
			if (world.equalsIgnoreCase("all")) {
				global = true;
			}
			ConfigurationNode groups = child.getNode("groups");
			for (ConfigurationNode group : groups.getChildrenMap().values()) {
				String name = group.getKey().toString();
				if (!this.groups.containsKey(world)) {
					Map<String, Group> map = Maps.newHashMap();
					map.put(name, new Group(world, group, null, global));
					this.groups.put(world, map);
				} else {
					Map<String, Group> map = this.groups.get(world);
					map.put(name, new Group(world, group, null, global));
					this.groups.put(world, map);
				}
			}
		}
	}

	public Groups(File path) {
		if (path.exists() && path.isFile()) {
			throw new IllegalArgumentException("Path is single file, use other constructor");
		}
		if (!path.exists()) {
			path.mkdirs();
		}
		for (File f : path.listFiles()) {
			if (!f.exists()) {
				continue;
			}
			String world = f.getName();
			boolean global = world.equalsIgnoreCase("all");
			if (f.isFile()) {
				ConfigurationNode groups = Utils.load(f).getNode("groups");
				for (ConfigurationNode group : groups.getChildrenMap().values()) {
					String name = group.getKey().toString();
					if (!this.groups.containsKey(world)) {
						Map<String, Group> map = Maps.newHashMap();
						map.put(name, new Group(world, group, null, global));
						this.groups.put(world, map);
					} else {
						Map<String, Group> map = this.groups.get(world);
						map.put(name, new Group(world, group, null, global));
						this.groups.put(world, map);
					}
				}
			} else {
				File f1 = new File(f, "groups");
				if (f1.exists() && f1.isFile()) {
					ConfigurationNode groups = Utils.load(f1).getNode("groups");
					for (ConfigurationNode group : groups.getChildrenMap().values()) {
						String name = group.getKey().toString();
						if (!this.groups.containsKey(world)) {
							Map<String, Group> map = Maps.newHashMap();
							map.put(name, new Group(world, group, null, global));
							this.groups.put(world, map);
						} else {
							Map<String, Group> map = this.groups.get(world);
							map.put(name, new Group(world, group, null, global));
							this.groups.put(world, map);
						}
					}
				} else {
					for (File g : f.listFiles()) {
						String name = g.getName();
						ConfigurationNode group = Utils.load(g);
						if (!this.groups.containsKey(world)) {
							Map<String, Group> map = Maps.newHashMap();
							map.put(name, new Group(world, group, null, global));
							this.groups.put(world, map);
						} else {
							Map<String, Group> map = this.groups.get(world);
							map.put(name, new Group(world, group, null, global));
							this.groups.put(world, map);
						}
					}
				}
			}
		}
	}

	public List<Group> getAllGroups() {
		List<Group> groups = Lists.newArrayList();
		for (Map<String, Group> g1 : this.groups.values()) {
			for (Group g : g1.values()) {
				groups.add(g);
			}
		}
		return groups;
	}

	public Group getMainGroup(User p) {
		if (!p.isOnline()) {
			Group cg = null;
			for (Group g : getGroups("all").values()) {
				if (g.getPermission().isEmpty() || p.hasPermission(g.getPermission())) {
					if (cg == null) {
						cg = g;
					} else if (cg.getPriority() < g.getPriority()) {
						cg = g;
					}
				}

			}
			return cg;
		} else {
			World w = p.getPlayer().get().getWorld();
			String wname = w.getName().toLowerCase();
			Group cg = null;
			for (Group g : getGroups(wname).values()) {
				if (g.getPermission().isEmpty() || p.hasPermission(g.getPermission())) {
					if (cg == null) {
						cg = g;
					} else if (cg.getPriority() < g.getPriority()) {
						cg = g;
					}
				}
			}
			return cg;
		}
	}

	public Group getMainGroup(User p, String world) {
		Ray.get().getLogger().info("getting group for player " + p.getName() + " in world " + world);
		Group cg = null;
		for (Group g : getGroups(world).values()) {
			if (g.getPermission().isEmpty() || p.hasPermission(g.getPermission())) {
				if (cg == null) {
					cg = g;
				} else if (cg.getPriority() < g.getPriority()) {
					cg = g;
				}
			}
		}
		Ray.get().getLogger()
				.info("group chosen for player " + p.getName() + " in world " + world + ": " + cg.getName());
		return cg;
	}

	public Group getGroup(String name, String world) {
		return getGroups(world).get(name);
	}

	public Map<String, Group> getGroups(User user) {
		Map<String, Group> out = Maps.newHashMap();
		Ray.get().getLogger().info("getting all main groups for user " + user.getName());
		for (String world : groups.keySet()) {
			out.put(world, getMainGroup(user, world));
		}
		return out;
	}

	public Map<String, Group> getGroups(String world) {
		Map<String, Group> out = groups.get(world) == null ? Maps.newHashMap() : groups.get(world);
		if (groups.containsKey("all")) {
			out.putAll(groups.get("all"));
		}
		return out;
	}
}
