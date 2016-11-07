package me.Wundero.Ray.listeners;
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

import java.util.Optional;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.framework.player.RayPlayer;

/**
 * Listens to events to make sure channels are joined and left properly.
 */
public class ChatChannelListener {

	/**
	 * Registers players to listen to all channels allowed, and joins the most
	 * populated channel
	 */
	@Listener(order = Order.EARLY)
	public void onJoin(ClientConnectionEvent.Join event) {
		if (!Ray.get().getChannels().useChannels()) {
			return;
		}
		ChatChannel mostIn = null;
		for (ChatChannel c : Ray.get().getChannels().getJoinableChannels(event.getTargetEntity(), true)) {
			if (c.isAutojoin()) {
				if (c.canJoin(event.getTargetEntity(), Optional.empty())) {
					c.addMember(event.getTargetEntity());
					RayPlayer.get(event.getTargetEntity()).addListenChannel(c);

					if (mostIn == null) {
						mostIn = c;
					} else {
						if (mostIn.compareTo(c) < 0) {
							mostIn = c;
						}
					}
				}
			}
		}
		if (mostIn != null) {
			if (RayPlayer.get(event.getTargetEntity()).getActiveChannel() == null) {
				RayPlayer.get(event.getTargetEntity()).setActiveChannel(mostIn);
			} else {
				RayPlayer.get(event.getTargetEntity()).applyChannel();
			}
		}

	}

	/**
	 * Remove the player from the channel.
	 */
	// TODO verify that this works.
	@Listener(order = Order.LATE)
	public void onLeave(ClientConnectionEvent.Disconnect event) {
		if (!Ray.get().getChannels().useChannels()) {
			return;
		}
		for (ChatChannel c : Ray.get().getChannels().getAllChannels()) {
			if (c.getMembersCollection().contains(event.getTargetEntity().getUniqueId())) {
				c.removeMember(event.getTargetEntity().getUniqueId());
			}
		}
	}

}
