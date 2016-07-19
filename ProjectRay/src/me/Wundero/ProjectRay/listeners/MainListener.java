package me.Wundero.ProjectRay.listeners;
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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;

import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.framework.Format;
import me.Wundero.ProjectRay.framework.FormatType;
import me.Wundero.ProjectRay.framework.RayPlayer;

public class MainListener {

	private boolean handle(FormatType t, MessageChannelEvent e, Map<String, Object> v, final Player p,
			MessageChannel channel) {
		if (p == null) {
			return false;
		}
		Format f = RayPlayer.getRay(p.getUniqueId()).getActiveGroup().getFormat(t);
		v = Ray.get().setVars(v, f.getTemplate(), p, false);
		final TextTemplate template = f.getTemplate();
		final Map<String, Object> args = Maps.newHashMap(v);
		MessageChannel newchan = MessageChannel.combined(channel, new MessageChannel() {

			@Override
			public Optional<Text> transformMessage(Object sender, MessageReceiver recipient, Text original,
					ChatType type) {
				// TODO checks to see if recip can see player msgs, etc.
				if (recipient instanceof Player) {
					args.putAll(Ray.get().setVars(args, template, (Player) recipient, true));
				} else {
					args.putAll(Ray.get().setVars(args, template, null, true));
				}
				Text t = template.apply(args).build();
				return Optional.of(t);
			}

			@Override
			public Collection<MessageReceiver> getMembers() {
				return Collections.emptyList();
			}

		});
		e.setChannel(newchan);
		return false;
	}

	@Listener
	public void onChat(MessageChannelEvent.Chat event) {
		Map<String, Object> vars = Maps.newHashMap();
		vars.put("message", event.getRawMessage());
		if (event.getCause().first(Player.class).isPresent()) {
			vars.put("player", Ray.get().getVariables().get("player", event.getCause().first(Player.class).get()));
		}
		event.setCancelled(handle(FormatType.CHAT, event, vars, (Player) vars.get("player"), event.getChannel().get()));
	}

	@Listener
	public void onJoin(ClientConnectionEvent.Join event) {

	}

	@Listener
	public void onQuit(ClientConnectionEvent.Disconnect event) {
	}

	@Listener
	public void onDeath(DestructEntityEvent.Death event) {
	}

	@Listener
	public void onKick(KickPlayerEvent event) {
	}

	@Listener
	public void onAch(GrantAchievementEvent.TargetPlayer event) {

	}

}
