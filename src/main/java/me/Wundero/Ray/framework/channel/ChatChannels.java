package me.Wundero.Ray.framework.channel;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * Singleton instance containing chat channels in memory.
 */
public class ChatChannels {
	private Map<String, ChatChannel> channels = Utils.sm();
	private ConfigurationNode node;
	private boolean useChannels = true;

	/**
	 * Load channels from a node.
	 */
	public void load(ConfigurationNode node) throws ObjectMappingException {
		if (node == null) {
			setUseChannels(false);
			return;
		}
		this.setNode(node);
		for (ConfigurationNode n : node.getChildrenMap().values()) {
			String name = n.getKey().toString();
			ChatChannel channel = n.getValue(TypeToken.of(ChatChannel.class));
			channels.put(name, channel);
		}
	}

	/**
	 * Save channels to a node.
	 */
	public void save() {
		if (node == null || !useChannels) {
			return;
		}
		for (ChatChannel ch : channels.values()) {
			try {
				node.getNode(ch.getName()).setValue(TypeToken.of(ChatChannel.class), ch);
			} catch (ObjectMappingException e) {
			}
		}
	}

	/**
	 * Get channels matching names.
	 */
	public List<ChatChannel> getChannels(Collection<String> names, boolean startWith) {
		List<ChatChannel> out = Utils.sl();
		for (String s : names) {
			ChatChannel c = getChannel(s, startWith);
			if (c != null) {
				out.add(c);
			}
		}
		return out;
	}

	/**
	 * Load a channel from a config node.
	 */
	public void addChannel(ConfigurationNode node) throws ObjectMappingException {
		String name = node.getKey().toString();
		ChatChannel channel = node.getValue(TypeToken.of(ChatChannel.class));
		channels.put(name, channel);
	}

	/**
	 * Get the channel mapped to the name. If startWith is true, it will be a
	 * more lenient check.
	 */
	public ChatChannel getChannel(String name, boolean startWith) {
		ChatChannel out = getChannel(name);
		if (out != null || !startWith) {
			return out;
		}
		for (ChatChannel c : channels.values()) {
			if (c.getName().toLowerCase().trim().startsWith(name.toLowerCase().trim())) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Return the channel based off of the name. Strict equals name comparison.
	 */
	public ChatChannel getChannel(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		return channels.get(name);
	}

	/**
	 * Return all the channels a player can join.
	 */
	public List<ChatChannel> getJoinableChannels(Player player, boolean showHidden) {
		List<ChatChannel> l = getAllChannels();
		List<ChatChannel> o = Utils.al();
		for (ChatChannel c : l) {
			if (c.isHidden() && !showHidden) {
				System.out.println("hidden");
				continue;
			}
			if (c.canJoin(player, Optional.empty())) {
				o.add(c);
			}

		}
		return o;
	}

	/**
	 * Return all the channels.
	 */
	public List<ChatChannel> getAllChannels() {
		return Utils.al(channels.values(), true);
	}

	/**
	 * Return the overall channel node.
	 */
	public ConfigurationNode getNode() {
		return node;
	}

	/**
	 * Set the overall channel node. NODE: This will NOT refresh channel memory
	 * representations.
	 */
	public void setNode(ConfigurationNode node) {
		this.node = node;
	}

	/**
	 * Whether channels are enabled.
	 */
	public boolean useChannels() {
		return useChannels;
	}

	private void setUseChannels(boolean useChannels) {
		this.useChannels = useChannels;
	}
}
