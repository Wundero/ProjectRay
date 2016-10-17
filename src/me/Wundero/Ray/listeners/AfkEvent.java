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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.player.RayPlayer;

/**
 * Represents an event which changes the state of the player being AFK.
 */
public class AfkEvent extends AbstractEvent implements Cancellable {

	private Cause cause;
	private Player afker;
	private boolean afk, cancelled;

	private AfkEvent(Cause c, Player a, boolean afk) {
		this.cause = c;
		this.afker = a;
		this.afk = afk;
		this.cancelled = false;
	}

	/**
	 * Fire an event to set whether the player is AFK.
	 */
	public static void setAFK(Player p, boolean afk) {
		AfkEvent e = new AfkEvent(Cause.source(Ray.get().getPlugin()).named("player", p).build(), p, afk);
		if (!Sponge.getEventManager().post(e)) {
			RayPlayer.get(e.getAfkPlayer()).setAFK(e.isAFK());
		}
	}

	/**
	 * Toggle whether the player is AFK.
	 */
	public static void toggleAFK(Player p) {
		setAFK(p, !RayPlayer.get(p).AFK());
	}

	/**
	 * Set whether the player is AFK.
	 */
	public void setAFK(boolean a) {
		this.afk = a;
	}

	/**
	 * @return whether the player is AFK.
	 */
	public boolean isAFK() {
		return afk;
	}

	/**
	 * Set who is going AFK.
	 */
	public void setAfkPlayer(Player p) {
		this.afker = p;
	}

	/**
	 * @return who is going AFK.
	 */
	public Player getAfkPlayer() {
		return afker;
	}

	/**
	 * @return the cause of the event.
	 */
	@Override
	public Cause getCause() {
		return cause;
	}

	/**
	 * @return whether the event is cancelled.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set whether the event is cancelled.
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
