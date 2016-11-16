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

/**
 * A class that, when registered as a listener, will block chat messages with
 * profanities.
 */
public class ChatFilter {

	// TODO possible pattern customization?
	private Pattern p = Utils.profanityPattern();
	private boolean filter = false;

	/**
	 * Filter chat messages.
	 */
	@Listener
	public void chat(MessageChannelEvent.Chat chat, @First Player sender) {
		if (!filter) {
			return;
		}
		if (p.matcher(chat.getRawMessage().toPlain()).find()) {
			if (!sender.hasPermission("ray.chatfilter.bypass")) {
				chat.setCancelled(true);
			}
		}
	}

	/**
	 * Toggle the filter.
	 */
	public void toggle() {
		filter = !filter;
	}

	/**
	 * Turn off the filter.
	 */
	public void ignore() {
		filter = false;
	}

	/**
	 * Turn on the filter.
	 */
	public void filter() {
		filter = true;
	}

	/**
	 * @return whether the filter is urunning.
	 */
	public boolean isFilter() {
		return filter;
	}

	/**
	 * Create a filter without a pattern to match against.
	 */
	public ChatFilter() {
		this(Optional.empty());
	}

	/**
	 * Create a filter with a pattern
	 */
	public ChatFilter(Pattern p) {
		this(Utils.wrap(p));
	}

	/**
	 * Create a filter.
	 */
	public ChatFilter(Optional<Pattern> opt) {
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
		if (opt.isPresent()) {
			p = opt.get();
		}
	}

}
