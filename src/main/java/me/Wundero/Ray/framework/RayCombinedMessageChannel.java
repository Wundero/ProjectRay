package me.Wundero.Ray.framework;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.util.GuavaCollectors;

import com.google.common.collect.ImmutableSet;

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

/**
 * Wrapper class for CombinedMessageChannels that allows any channel to cancel
 * the message sending by returning Optional.empty().
 */
public class RayCombinedMessageChannel implements MessageChannel {

	protected final Collection<MessageChannel> channels;

	public RayCombinedMessageChannel(MessageChannel... channels) {
		this(Arrays.asList(channels), true);
	}

	public RayCombinedMessageChannel(Collection<? extends MessageChannel> channels, boolean collection) {
		this.channels = ImmutableSet.copyOf(channels);
	}

	@Override
	public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original,
			ChatType type) {
		Text text = original;
		for (MessageChannel channel : this.channels) {
			text = channel.transformMessage(sender, recipient, text, type).orElse(null);
			if (text == null) {
				break;
			}
		}

		return Optional.ofNullable(text);
	}

	@Override
	public Collection<MessageReceiver> getMembers() {
		return this.channels.stream().flatMap(channel -> channel.getMembers().stream())
				.collect(GuavaCollectors.toImmutableSet());

	}

}
