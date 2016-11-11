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
import org.spongepowered.api.event.impl.AbstractMessageEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.utils.TextUtils;

/**
 * Represents an event which changes the state of the player being AFK.
 */
public class AfkEvent extends AbstractMessageEvent implements Cancellable {

	private Cause cause;
	private Player afker;
	private boolean afk, mafk, cancelled = false, mcancelled = false;

	private AfkEvent(Cause c, Player a, boolean afk) {
		this.cause = c;
		this.afker = a;
		this.afk = afk;
		this.mafk = afk;
		super.originalMessage = a.getDisplayNameData().displayName().exists()
				? Text.of(TextColors.GRAY, " * ")
						.concat(a.getDisplayNameData().displayName().get()
								.concat(Text.of(TextColors.GRAY, " is no" + (afk ? "w" : " longer") + " AFK!")))
				: Text.of(TextColors.GRAY, " * " + a.getName() + " is no" + (afk ? "w" : " longer") + " AFK!");
		super.formatter = new MessageEvent.MessageFormatter(originalMessage);
	}

	/**
	 * Fire an event to set whether the player is AFK.
	 */
	public static void setAFK(Player p, boolean afk) {
		AfkEvent e = new AfkEvent(Cause.source(Ray.get().getPlugin()).named("player", p).build(), p, afk);
		if (!Sponge.getEventManager().post(e)) {
			RayPlayer.get(e.getAfkPlayer()).setAFK(e.isAFK());
			Text msg = e.getMessage();
			if (e.mafk != e.afk) {
				if (TextUtils.equals(msg, e.getOriginalMessage())) {
					msg = e.getAfkPlayer().getDisplayNameData().displayName().exists()
							? Text.of(TextColors.GRAY, " * ")
									.concat(e.getAfkPlayer().getDisplayNameData().displayName().get()
											.concat(Text.of(TextColors.GRAY,
													" is no" + (e.afk ? "w" : " longer") + " AFK!")))
							: Text.of(TextColors.GRAY, " * " + e.getAfkPlayer().getName() + " is no"
									+ (e.afk ? "w" : " longer") + " AFK!");
				}
			}
			if (!e.mcancelled) {
				for (Player px : Sponge.getServer().getOnlinePlayers()) {
					px.sendMessage(msg);
				}
			}
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

	/**
	 * Get the original message.
	 */
	@Override
	public Text getOriginalMessage() {
		return this.originalMessage;
	}

	/**
	 * Get whether the message will be sent.
	 */
	@Override
	public boolean isMessageCancelled() {
		return this.mcancelled;
	}

	/**
	 * Set whether the message will be sent.
	 */
	@Override
	public void setMessageCancelled(boolean cancelled) {
		this.mcancelled = cancelled;
	}

	/**
	 * Get the message formatter.
	 */
	@Override
	public MessageFormatter getFormatter() {
		return this.formatter;
	}

}
