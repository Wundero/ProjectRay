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

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.DataHolder;
import me.Wundero.ProjectRay.Ray;

public class RayPlayer extends DataHolder {

	// TODO conversations

	private static HashMap<UUID, RayPlayer> cache = Maps.newHashMap();

	public static RayPlayer getRay(UUID u) {
		if (!cache.containsKey(u)) {
			User u2 = (User) Sponge.getServer().getPlayer(u).orElse(null);
			if (u2 == null) {
				return null;
			}
			return new RayPlayer(u2);
		} else
			return cache.get(u);
	}

	public void updateGroup() {
		this.setGroup(Ray.get().getGroups().getMainGroup(user));
	}

	private User user;
	private Group group;

	public RayPlayer(User u) {
		this.setUser(u);
		this.uuid = u.getUniqueId();
		cache.put(uuid, this);
		this.setGroup(Ray.get().getGroups().getMainGroup(u));
	}

	public UUID getUniqueId() {
		return getUUID();
	}

	public User getUser() {
		return user;
	}

	private void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
}
