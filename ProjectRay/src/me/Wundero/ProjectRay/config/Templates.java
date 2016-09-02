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

public class Templates {

	// pseudo-enum methods that create configs when they are not already there.

	public static Template DEFAULT(Template.Builder t) {
		// basic chat format
		return t.withGroup("default").withPriority(0).withFormat(DefaultFormats.BASIC_CHAT).build().build();
	}

	public static Template ADVANCED(Template.Builder t) {
		// chat, join, leave and msg
		return t.withGroup("default").withPriority(0).withFormat(DefaultFormats.CHAT).withFormat(DefaultFormats.JOIN)
				.withFormat(DefaultFormats.LEAVE).withFormat(DefaultFormats.MSG_RECEIVE)
				.withFormat(DefaultFormats.MSG_SEND).build().build();
	}

	public static Template MULTI_GROUP(Template.Builder t) {
		// chat join and leave for two groups - not gonna use def formats
		return t.withGroup("default").withPriority(0).withFormat(DefaultFormats.CHAT).withFormat(DefaultFormats.JOIN)
				.withFormat(DefaultFormats.LEAVE).withFormat(DefaultFormats.MSG_RECEIVE)
				.withFormat(DefaultFormats.MSG_SEND).build().withGroup("admin").withPriority(1).withParent("default")
				.withPermission("ray.group.admin").withFormat("chat")
				.withText(
						Text.of(TextColors.BLACK, "[", TextColors.RED, "Admin", TextColors.BLACK, "]", TextColors.AQUA))
				.withArg(DefaultArgs.DISPLAYNAME).withText(Text.of(" "))
				.withArg(TextTemplate.arg("message").color(TextColors.GRAY)).build().withFormat("join")
				.withText(
						Text.of(TextColors.BLACK, "[", TextColors.RED, "Admin", TextColors.BLACK, "]", TextColors.AQUA))
				.withArg(DefaultArgs.DISPLAYNAME)
				.withText(Text.of(" "), Text.builder("has joined!").color(TextColors.AQUA).build()).build()
				.withFormat("leave")
				.withText(
						Text.of(TextColors.BLACK, "[", TextColors.RED, "Admin", TextColors.BLACK, "]", TextColors.AQUA))
				.withArg(DefaultArgs.DISPLAYNAME)
				.withText(Text.of(" "), Text.builder("has left!").color(TextColors.RED).build()).build().build()
				.build();
	}
}
