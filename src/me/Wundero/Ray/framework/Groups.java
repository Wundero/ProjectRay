package me.Wundero.Ray.framework;
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

import java.util.List;
import java.util.Map;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class Groups {
	private Map<String, Map<String, Group>> groups = Utils.sm();

	public Groups(ConfigurationNode node) {
		for (ConfigurationNode child : node.getChildrenMap().values()) {
			boolean global = false;
			String world = child.getKey().toString();
			if (world.equalsIgnoreCase("all")) {
				global = true;
			}
			ConfigurationNode groups = child.getNode("groups");
			for (ConfigurationNode group : groups.getChildrenMap().values()) {
				String name = group.getKey().toString();
				if (!this.groups.containsKey(world)) {
					Map<String, Group> map = Utils.sm();
					map.put(name, new Group(world, group, global));
					this.groups.put(world, map);
				} else {
					Map<String, Group> map = this.groups.get(world);
					map.put(name, new Group(world, group, global));
					this.groups.put(world, map);
				}
			}
		}
	}

	public Group load(ConfigurationNode groupNode) {
		String worldname = groupNode.getParent().getParent().getKey().toString();
		Group group = new Group(worldname, groupNode, worldname.equalsIgnoreCase("all"));
		Map<String, Group> map = groups.getOrDefault(worldname, Utils.sm());
		map.put(group.getName(), group);
		groups.put(worldname, map);
		return group;
	}

	public List<Group> getAllGroups() {
		List<Group> groups = Utils.sl();
		for (Map<String, Group> g1 : this.groups.values()) {
			for (Group g : g1.values()) {
				groups.add(g);
			}
		}
		return groups;
	}

	public Group getMainGroup(User p) {
		if (!p.isOnline()) {
			return getMainGroup(p, "all");
		} else {
			World w = p.getPlayer().get().getWorld();
			String wname = w.getName().toLowerCase();
			return getMainGroup(p, wname);
		}
	}

	public Group getMainGroup(User p, String world) {
		Group cg = null;
		for (Group g : getGroups(world).values()) {
			// TODO ensure non null group returns for first server join
			if (!g.getPermission().isPresent() || p.hasPermission(g.getPermission().get())) {
				if (cg == null) {
					cg = g;
				} else if (cg.getPriority() < g.getPriority()) {
					cg = g;
				}
			}
		}
		return cg;
	}

	public Group getGroup(String name, String world) {
		return getGroups(world).get(name);
	}

	public Map<String, Group> getGroups(User user) {
		Map<String, Group> out = Utils.sm();
		for (String world : groups.keySet()) {
			if (getMainGroup(user, world) == null) {
				continue;
			}
			out.put(world, getMainGroup(user, world));
		}
		if (out.isEmpty()) {
			Ray.get().getLogger().warn("No groups loaded for player: " + user.getName());
		}
		return out;
	}

	public Map<String, Group> getGroups(String world) {
		Map<String, Group> out = groups.get(world) == null ? Utils.sm() : groups.get(world);
		if (!world.equalsIgnoreCase("all")) {
			out.putAll(getGroups("all"));
		}
		return out;
	}
}
