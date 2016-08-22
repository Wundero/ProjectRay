package me.Wundero.ProjectRay.framework;
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
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.util.GuavaCollectors;

import me.Wundero.ProjectRay.utils.Utils;

public class RayIntersectedMessageChannel implements MessageChannel {
	protected final List<MessageChannel> channels;

	public RayIntersectedMessageChannel(MessageChannel... channels) {
		this.channels = Utils.sl(channels);
	}

	public RayIntersectedMessageChannel(Collection<MessageChannel> channels) {
		this.channels = Utils.sl(channels);
	}

	@Override
	public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original,
			ChatType type) {
		Text text = original;
		for (MessageChannel channel : this.channels) {
			text = channel.transformMessage(sender, recipient, text, type).orElse(null);
			if(text==null) {
				break;
			}
		}

		return Optional.ofNullable(text);
	}

	@Override
	public Collection<MessageReceiver> getMembers() {
		if (channels == null || channels.isEmpty()) {// prevents IOORE and NPE
			return Utils.sl();
		}
		Collection<MessageReceiver> out = channels.get(0).getMembers();
		return channels.stream().flatMap(channel -> channel.getMembers().stream()).filter(r -> out.contains(r))
				.collect(GuavaCollectors.toImmutableSet());
	}

}
