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

import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;

public class ChatFilter {

	private Pattern p = Utils.profanityPattern();
	private boolean filter = false;

	@Listener
	public void chat(MessageChannelEvent.Chat chat, @First Player sender) {
		if (p.matcher(chat.getRawMessage().toPlain()).find()) {
			if (!sender.hasPermission("ray.chatfilter.bypass")) {
				chat.setCancelled(true);
			}
		}
	}

	public void toggle() {
		filter = !filter;
	}

	public void ignore() {
		filter = false;
	}

	public void filter() {
		filter = true;
	}
	
	public boolean isFilter() {
		return filter;
	}

	public ChatFilter() {
		this(Optional.empty());
	}

	public ChatFilter(Pattern p) {
		this(Utils.wrap(p));
	}

	public ChatFilter(Optional<Pattern> opt) {
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
		if (opt.isPresent()) {
			p = opt.get();
		}
	}

}
