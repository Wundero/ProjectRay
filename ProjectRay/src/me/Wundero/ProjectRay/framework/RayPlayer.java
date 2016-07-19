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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.channel.MessageChannel;

import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.Ray;

public class RayPlayer {

	// TODO conversations
	// TODO file saving - save to individual files with world - group map

	private boolean conversing = false;
	private static HashMap<UUID, RayPlayer> cache = Maps.newHashMap();

	public static RayPlayer getRay(UUID u) {
		if (!cache.containsKey(u)) {
			Optional<Player> p = Sponge.getServer().getPlayer(u);
			if (!p.isPresent()) {
				return null;
			}
			Player u2 = p.get();
			return new RayPlayer(u2);
		} else
			return cache.get(u);
	}

	public static RayPlayer getRay(User u) {
		if (!cache.containsKey(u.getUniqueId())) {
			return new RayPlayer(u);
		}
		return cache.get(u.getUniqueId());
	}

	private User user;
	private UUID uuid;
	private Map<String, Group> groups;

	public RayPlayer(User u) {
		this.setUser(u);
		this.uuid = u.getUniqueId();
		cache.put(uuid, this);
		this.setGroups(Ray.get().getGroups().getGroups(u));
	}

	public UUID getUniqueId() {
		return getUUID();
	}

	private UUID getUUID() {
		return uuid;
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
	public Map<String, Group> getGroups() {
		return groups;
	}

	public Group getActiveGroup() {
		if (!user.isOnline()) {
			return null;
		}
		return getGroups().get(user.getPlayer().get().getWorld().getName());
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return the conversing
	 */
	public boolean isConversing() {
		return conversing;
	}

	/**
	 * @param conversing
	 *            the conversing to set
	 */
	public void setConversing(boolean conversing) {
		if (!user.isOnline()) {
			conversing = false;
			return;
		}
		this.conversing = conversing;
		if (conversing) {
			user.getPlayer().get().setMessageChannel(MessageChannel.TO_NONE);
		}
	}
}
