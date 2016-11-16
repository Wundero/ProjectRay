package me.Wundero.Ray.features;
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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;

import me.Wundero.Ray.Ray;

/**
 * A class that, when activated, will block any chat messages sent by players
 * from showing.
 */
public class ChatLock {

	/**
	 * Create a new lock; this will automatically register the listener.
	 */
	public ChatLock() {
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
	}

	private boolean l = false;

	/**
	 * Block chat if toggled.
	 */
	@Listener
	public void onChat(MessageChannelEvent.Chat chat, @First Player s) {
		if (l) {
			if (!s.hasPermission("ray.chatlock.bypass")) {
				chat.setCancelled(true);
			}
		}
	}

	/**
	 * Toggle the lock.
	 */
	public void toggle() {
		l = !l;
	}

	/**
	 * Turn off the lock.
	 */
	public void unlock() {
		l = false;
	}

	/**
	 * Turn on the lock.
	 */
	public void lock() {
		l = true;
	}

	/**
	 * @return whether the lock is active
	 */
	public boolean isLocked() {
		return l;
	}

}
