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

import java.util.Map;

import com.google.common.collect.Maps;

import ninja.leaping.configurate.ConfigurationNode;

public class Groups {
	private Map<String, Map<String, Group>> groups = Maps.newHashMap();

	// pass "worlds" node
	public Groups(ConfigurationNode node) {
		for (ConfigurationNode child : node.getChildrenList()) {
			String world = child.getKey().toString();
			ConfigurationNode groups = child.getNode("groups");
			for (ConfigurationNode group : groups.getChildrenList()) {
				String name = group.getKey().toString();
				if (!this.groups.containsKey(world)) {
					Map<String, Group> map = Maps.newHashMap();
					map.put(name, new Group(world, group));
					this.groups.put(world, map);
				} else {
					Map<String, Group> map = this.groups.get(world);
					map.put(name, new Group(world, group));
					this.groups.put(world, map);
				}
			}
		}
	}

	public Group getGroup(String name, String world) {
		if (!groups.containsKey(world)) {
			return null;
		}
		return getGroups(world).get(name);
	}

	public Map<String, Group> getGroups(String world) {
		return groups.get(world);
	}
}
