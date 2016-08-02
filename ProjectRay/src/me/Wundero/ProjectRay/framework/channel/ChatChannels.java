package me.Wundero.ProjectRay.framework.channel;
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

import org.spongepowered.api.entity.living.player.Player;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class ChatChannels {
	private Map<String, ChatChannel> channels = Utils.sm();
	private ConfigurationNode node;

	public void load(ConfigurationNode node) throws ObjectMappingException {
		if (node == null) {
			return;
		}
		this.setNode(node);
		for (ConfigurationNode n : node.getChildrenMap().values()) {
			String name = n.getKey().toString();
			ChatChannel channel = n.getValue(TypeToken.of(ChatChannel.class));
			channels.put(name, channel);
		}
	}

	public void addChannel(ConfigurationNode node) throws ObjectMappingException {
		String name = node.getKey().toString();
		ChatChannel channel = node.getValue(TypeToken.of(ChatChannel.class));
		channels.put(name, channel);
	}

	public ChatChannel getChannel(String name, boolean startWith) {
		ChatChannel out = getChannel(name);
		if (out != null || !startWith) {
			return out;
		}
		for (ChatChannel c : channels.values()) {
			if (c.getName().startsWith(name)) {
				return c;
			}
		}
		return null;
	}

	public ChatChannel getChannel(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		return channels.get(name);
	}

	public List<ChatChannel> getJoinableChannels(Player player, boolean showHidden) {
		List<ChatChannel> l = getAllChannels();
		List<ChatChannel> o = Utils.sl();
		for (ChatChannel c : l) {
			if (c.isHidden() && !showHidden) {
				continue;
			}
			if (c.canJoin(player)) {
				o.add(c);
			}

		}
		return o;
	}

	public List<ChatChannel> getAllChannels() {
		return Utils.sl(channels.values());
	}

	public ConfigurationNode getNode() {
		return node;
	}

	public void setNode(ConfigurationNode node) {
		this.node = node;
	}
}
