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

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class ChatChannels {
	private Map<String, ChatChannel> channels = Utils.sm();

	public void load(ConfigurationNode node) {
		// not implemented yet as i need to decide on how to structure config,
		// and also how to deal with channels as a whole
	}

	public ChatChannel getChannel(String name) {
		return channels.get(name);
	}

	public List<ChatChannel> getJoinableChannels(Player player) {
		List<ChatChannel> l = getAllChannels();
		List<ChatChannel> o = Utils.sl();
		synchronized (l) {
			for (ChatChannel c : l) {
				if (player.hasPermission(c.getPermission())) {
					o.add(c);
				}
			}
		}
		return o;
	}

	public List<ChatChannel> getAllChannels() {// due to clone synchronized
												// blocks probably not necessary
		return Utils.sl(channels.values());
	}
}
