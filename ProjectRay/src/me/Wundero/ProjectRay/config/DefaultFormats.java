package me.Wundero.ProjectRay.config;
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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.framework.format.FormatType;

public class DefaultFormats {

	public static final DefaultFormat BASIC_CHAT = new DefaultFormat("chat", FormatType.CHAT)
			.with(TextTemplate.arg("player")).with(" ").with(TextTemplate.arg("message"));
	public static final DefaultFormat CHAT = new DefaultFormat("chat", FormatType.CHAT).with(DefaultArgs.CHANNEL)
			.with(" ").with(DefaultArgs.DISPLAYNAME).with(" ").with(TextTemplate.arg("message").color(TextColors.GRAY));
	public static final DefaultFormat JOIN = new DefaultFormat("join", FormatType.JOIN).with(DefaultArgs.DISPLAYNAME)
			.with(" ").with(Text.of(TextColors.AQUA, "has joined!"));
	public static final DefaultFormat LEAVE = new DefaultFormat("leave", FormatType.LEAVE).with(DefaultArgs.DISPLAYNAME)
			.with(" ").with(Text.of(TextColors.RED, "has left!"));
	public static final DefaultFormat MSG_RECEIVE = new DefaultFormat("receive", FormatType.MESSAGE_RECEIVE)
			.with(DefaultArgs.DISPLAYNAME).with(Text.of(TextColors.AQUA, " to you")).with(Text.of(TextColors.GRAY, ":"))
			.with(TextTemplate.arg("message").color(TextColors.GRAY));
	public static final DefaultFormat MSG_SEND = new DefaultFormat("send", FormatType.MESSAGE_SEND)
			.with(Text.of(TextColors.AQUA, "You to ")).with(DefaultArgs.RECIP_DISPLAYNAME)
			.with(Text.of(TextColors.GRAY, ":")).with(TextTemplate.arg("message").color(TextColors.GRAY));

}
