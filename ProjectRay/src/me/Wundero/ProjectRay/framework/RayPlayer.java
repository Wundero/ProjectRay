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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.channel.MessageChannel;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.animation.Animation;
import me.Wundero.ProjectRay.framework.channel.ChatChannel;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class RayPlayer {

	// TODO conversations
	// TODO file saving - save to individual files with world - group map

	private boolean conversing = false;
	private static Map<UUID, RayPlayer> cache = Utils.sm();

	public static RayPlayer getRay(UUID u) {
		if (!cache.containsKey(u)) {
			Optional<Player> p = Sponge.getServer().getPlayer(u);
			if (!p.isPresent()) {
				UserStorageService storage = Ray.get().getPlugin().getGame().getServiceManager()
						.provide(UserStorageService.class).get();
				Optional<User> opt = storage.get(u);
				if (opt.isPresent()) {
					return new RayPlayer(opt.get());
				}
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

	public static void updateTabs() {
		for (RayPlayer p : cache.values()) {
			if (p.user.isOnline() && p.user.getPlayer().isPresent()) {
				p.updateTab();
			}
		}
	}

	public void updateTab() {
		Task.builder().execute(tabTask).submit(Ray.get().getPlugin());
	}

	public static void saveAll() throws ObjectMappingException {
		for (RayPlayer p : cache.values()) {
			p.save();
		}
	}

	public static RayPlayer get(UUID u) {
		return getRay(u);
	}

	public static RayPlayer get(User u) {
		return getRay(u);
	}

	private User user;
	private UUID uuid;
	private Map<String, Group> groups;
	private Optional<RayPlayer> lastMessaged = Optional.empty();
	private List<UUID> ignore = Utils.sl();
	private ConfigurationNode config;
	private ChatChannel activeChannel = null;
	private Runnable tabTask;
	private Animation tabhfAnimation;

	public boolean isIgnoring(RayPlayer player) {
		return ignore.contains(player.uuid);
	}

	public boolean unignore(RayPlayer player) {
		return ignore.remove(player.uuid);
	}

	public boolean ignore(RayPlayer player) {
		if (ignore.contains(player.uuid)) {
			return false;
		}
		return ignore.add(player.uuid);
	}

	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	public void setActiveChannel(ChatChannel channel) {
		if (channel == null) {
			return;
		}
		activeChannel = channel;
		if (user.isOnline() && user.getPlayer().isPresent()) {
			user.getPlayer().get().setMessageChannel(activeChannel);
		}
	}

	public void load() throws ObjectMappingException {
		if (config == null) {
			return;
		}
		ConfigurationNode i = config.getNode("ignoring");
		ignore = Utils.sl(i.getList(TypeToken.of(UUID.class)));
		setActiveChannel(Ray.get().getChannels().getChannel(config.getNode("channel").getString()));
	}

	public void save() throws ObjectMappingException {
		if (config == null) {
			return;
		}
		config.getNode("ignoring").setValue(ignore);
		config.getNode("channel").setValue(activeChannel == null ? null : activeChannel.getName());
		config.getNode("lastname").setValue(user.getName());
		// TODO save displayname
	}

	public RayPlayer(User u) {
		// TODO pass config node that's loaded already if single file for all
		// players is desired
		File p = new File(Ray.get().getPlugin().getConfigDir().toFile(), "players");
		File f = new File(p, u.getUniqueId() + ".conf");
		if (!p.exists()) {
			p.mkdirs();
		}
		if (f.exists()) {
			config = Utils.load(f);
			try {
				load();
			} catch (Exception e) {
				Utils.printError(e);
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				Utils.printError(e);
			}
			config = Utils.load(f);
		}
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
		return gg((user.getPlayer().get()).getWorld().getName()) == null ? gg("all")
				: gg((user.getPlayer().get()).getWorld().getName());
	}

	private Group gg(String world) {
		return getGroups().get(world);
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

	public void reloadGroups() {
		this.setGroups(Ray.get().getGroups().getGroups(this.getUser()));
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

	/**
	 * @return the lastMessaged
	 */
	public Optional<RayPlayer> getLastMessaged() {
		return lastMessaged;
	}

	/**
	 * @param lastMessaged
	 *            the lastMessaged to set
	 */
	public void setLastMessaged(Optional<RayPlayer> lastMessaged, boolean recurse) {
		if (!lastMessaged.isPresent()) {
			return;
		}
		if (recurse) {
			lastMessaged.get().setLastMessaged(Optional.of(this), false);
		}
		this.lastMessaged = lastMessaged;
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

	/**
	 * @return the tabTask
	 */
	public Runnable getTabTask() {
		return tabTask;
	}

	/**
	 * @param tabTask
	 *            the tabTask to set
	 */
	public void setTabTask(Runnable tabTask) {
		this.tabTask = tabTask;
	}

	/**
	 * @return the tabhfAnimation
	 */
	public Animation getTabAnimation() {
		return tabhfAnimation;
	}

	/**
	 * @param tabhfAnimation
	 *            the tabhfAnimation to set
	 */
	public void setTabAnimation(Animation tabhfAnimation) {
		this.tabhfAnimation = tabhfAnimation;
	}
}
