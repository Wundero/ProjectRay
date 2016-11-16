package me.Wundero.Ray.utils;
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

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import me.Wundero.Ray.Ray;

/**
 * A cache to store a UUID to User map. Holds references to offline players
 * [user holds said information].
 */
public class UserCache extends OptionalMap<UUID, User> {

	public UserCache() {
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
	}

	/**
	 * Update reference to player.
	 */
	public void update(UUID u) {
		this.put(u, Utils.getUser(u, false).get());
	}

	/**
	 * Update on join.
	 */
	@Listener(order = Order.POST)
	public void join(ClientConnectionEvent.Join event) {
		update(event.getTargetEntity().getUniqueId());
	}

	/**
	 * Update on leave.
	 */
	@Listener(order = Order.POST)
	public void quit(ClientConnectionEvent.Disconnect event) {
		update(event.getTargetEntity().getUniqueId());
	}

	/**
	 * Update on death.
	 */
	@Listener(order = Order.POST)
	public void death(DestructEntityEvent.Death event) {
		if (event.getTargetEntity() instanceof Player)
			update(event.getTargetEntity().getUniqueId());
	}

}
