package me.Wundero.Ray.framework.trigger;

import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.Group;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.objectmapping.Setting;

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

public class EventTrigger extends Trigger {

	public EventTrigger() {
	}

	public EventTrigger(String event) {
		this.event = event;
	}

	@Setting
	private String event;
	// TODO event filters + more data
	// TODO choose targets - sender, event target, event targets, all?
	// TODO mod event opts - cancel, cancelmsg, changecmd, etc.
	/*
	 * Example triggers: player block break/place/change [break is ex] - event:
	 * blockbreak - fitler for certain broken blocks - vars contains broken
	 * block - cancel event opt - cancel msg opt player join - filter first join
	 * for welcome
	 */

	private static Map<String, Class<? extends Event>> presetClasses = Utils.sm();

	static {
		presetClasses.put("join", ClientConnectionEvent.Join.class);
		presetClasses.put("leave", ClientConnectionEvent.Disconnect.class);
		presetClasses.put("chat", MessageChannelEvent.Chat.class);
		presetClasses.put("death", DestructEntityEvent.Death.class);
		presetClasses.put("achievement", GrantAchievementEvent.TargetPlayer.class);
		presetClasses.put("kick", KickPlayerEvent.class);
	}

	@Override
	public void setGroup(Group g) {
		super.setGroup(g);
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
	}

	@Listener
	public void onEvent(Event e) {
		Class<?> c = e.getClass();
		if (!c.getName().equalsIgnoreCase(event)) {
			if (presetClasses.containsKey(event)) {
				if (!presetClasses.get(event).isAssignableFrom(c)) {
					return;
				}
			} else {
				if (!c.getSimpleName().equalsIgnoreCase(event)) {
					return;
				}
			}
		}
		// TODO event loading
		trigger(null, Utils.al());
	}

}
