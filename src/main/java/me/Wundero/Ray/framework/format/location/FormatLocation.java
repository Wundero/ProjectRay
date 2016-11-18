package me.Wundero.Ray.framework.format.location;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import me.Wundero.Ray.framework.format.Format;

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
 * Represents a place to which a message can be sent.
 */
public abstract class FormatLocation {

	private final String name;

	public FormatLocation(String name) {
		this.name = name;
	}

	/**
	 * @return the location's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Send a message to the location.
	 */
	public abstract boolean send(Text text, MessageReceiver target, Format f, Optional<Object> sender,
			Optional<UUID> textId);

	/**
	 * Send a message to the location.
	 */
	public boolean send(Text text, MessageReceiver target, Format format, Optional<Object> sender) {
		return send(text, target, format, sender, Optional.empty());
	}

}
