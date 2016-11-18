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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.context.FormatContexts;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.utils.Utils;

/**
 * Listener for AFK cancelling events. Runs an async AFK checker to see who
 * is/isn't AFK and who should be kicked.
 */
public class AfkListener {

	private Map<UUID, Long> lastMoved = Utils.sm();
	private Task checkTask = null;
	private boolean cancelMovement;

	/**
	 * Create a new listener. Automatically registered. Timer will set players
	 * as AFK after a certain time. Kick will kick players after a certain time.
	 * Set either to -1 to disable their effects. Kick and Timer are in
	 * milliseconds. Cancel movement boolean decides whether to prevent players
	 * from moving if AFK.
	 */
	public AfkListener(final int timer, final int kick, boolean cancelMovement) {
		this.cancelMovement = cancelMovement;
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
		checkTask = Task.builder().interval(1, TimeUnit.SECONDS).async().execute(() -> {
			for (Entry<UUID, Long> e : lastMoved.entrySet()) {
				Optional<User> u = Utils.getUser(e.getKey());
				if (!u.isPresent() || !u.get().isOnline() || !u.get().getPlayer().isPresent()) {
					continue;
				}
				Player p = u.get().getPlayer().get();
				if ((System.currentTimeMillis() - e.getValue()) > TimeUnit.SECONDS.toMillis(kick) && kick > 0) {
					p.kick(Text.of("You were AFK for too long!"));
				} else if ((System.currentTimeMillis() - e.getValue()) > TimeUnit.SECONDS.toMillis(timer)
						&& timer > 0) {
					if (!RayPlayer.get(p).AFK()) {
						AfkEvent.setAFK(p, true);
					}
				}
			}
		}).submit(Ray.get().getPlugin());
		Ray.get().registerTask(checkTask);
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onMove(MoveEntityEvent event) {
		if (event.getTargetEntity() instanceof Player) {
			if (cancelMovement && RayPlayer.get((Player) event.getTargetEntity()).AFK()) {
				event.setCancelled(true);
				return;
			}
			update((Player) event.getTargetEntity());
		}
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onCommand(SendCommandEvent event) {
		if (event.getCommand().equalsIgnoreCase("afk")) {
			return;
		}
		if (event.getCause().containsType(Player.class)) {
			update(event.getCause().first(Player.class).get());
		}
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onTalk(MessageChannelEvent.Chat event) {
		if (event.getCause().containsNamed("formatcontext")) {
			FormatContext ctx = event.getCause().get("formatcontext", FormatContext.class).get();
			if (ctx == FormatContexts.AFK) {
				return;
			}
		}
		if (event.getCause().containsType(Player.class)) {
			update(event.getCause().first(Player.class).get());
		}
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onSleep(SleepingEvent event) {
		if (event.getTargetEntity() instanceof Player) {
			update((Player) event.getTargetEntity());
		}
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onInteract(InteractEntityEvent event) {
		if (event.getCause().containsType(Player.class)) {
			update(event.getCause().first(Player.class).get());
		}
	}

	/**
	 * Cancel AFK on event.
	 */
	@Listener(order = Order.POST)
	public void onFish(FishingEvent event) {
		Optional<UUID> creator = event.getFishHook().getCreator();
		if (creator.isPresent()) {
			User u = Utils.getUser(creator.get()).orElse(null);
			if (u != null && u.isOnline() && u.getPlayer().isPresent()) {
				update(u.getPlayer().get());
			}
		}
	}

	private void update(Player p) {
		unAFK(p);
		updateTime(p);
	}

	private void unAFK(Player p) {
		if (RayPlayer.get(p).AFK()) {
			AfkEvent.setAFK(p, false);
		}
	}

	private void updateTime(Player p) {
		this.lastMoved.put(p.getUniqueId(), System.currentTimeMillis());
	}

}
