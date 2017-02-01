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

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.config.Rootable;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Singleton instance containing all groups in memory.
 */
@ConfigSerializable
public class Groups implements Rootable {

	public static TypeToken<Groups> type = TypeToken.of(Groups.class);

	@Setting
	private Map<String, Group> groups = Utils.sm();

	/**
	 * Clear all group references.
	 */
	public void terminate() {
		groups.clear();
	}
	
	public void addGroup(String name, Group group) {
		if(name==null || group==null) {
			return;
		}
		groups.put(name, group);
		group.setName(name);
	}

	/**
	 * Return all groups from all worlds.
	 */
	public List<Group> getGroups() {
		return Utils.al(groups.values(), true);
	}

	/**
	 * Get the primary group for a player on a world.
	 */
	public Group getMainGroup(User p) {
		Group cg = null;
		for (Group g : getGroups()) {
			if (g == null) {
				continue;
			}
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

	public Map<String, Group> getGroupsMap() {
		return Utils.hm(groups);
	}

	public Group getGroup(String name) {
		return groups.get(name);
	}

	@Override
	public void applyRoot(String name, ConfigurationNode root) {
		for(Map.Entry<String, Group> e : groups.entrySet()) {
			e.getValue().applyRoot(e.getKey(), root.getNode("groups", e.getKey()));
		}
	}
}
