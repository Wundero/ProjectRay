package me.Wundero.ProjectRay.commands;
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

import java.util.List;
import java.util.Map;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import me.Wundero.ProjectRay.utils.Utils;

public class Commands {

	private static Map<List<String>, CommandCallable> children = Utils.sm();
	static {
		children.put(Utils.sl("format"), CommandSpec.builder().permission("ray.formatbuilder")
				.description(Text.of("Create a new format.")).executor(new FormatConversationCommand()).build());
		children.put(Utils.sl("channel"), CommandSpec.builder().permission("ray.channelbuilder")
				.description(Text.of("Create a new channel")).executor(new ChannelConversationCommand()).build());
		children.put(Utils.sl("fake"),
				CommandSpec.builder().permission("ray.fake").description(Text.of("Send a fake message."))
						.executor(new FakeMessageCommand())
						.arguments(GenericArguments.flags().valueFlag(GenericArguments.player(Text.of("player")), "p")
								.buildWith(GenericArguments.seq(GenericArguments.string(Text.of("type")),
										GenericArguments.remainingJoinedStrings(Text.of("message")))))
						.build());
		/*
		 * children.put(Lists.newArrayList("m", "msg", "message", "t", "tell",
		 * "w", "whisper"),
		 * CommandSpec.builder().permission("ray.message").description(Text.of(
		 * "Message a player."))
		 * .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of(
		 * "player"))),
		 * GenericArguments.remainingJoinedStrings(Text.of("message")))
		 * .executor(new MessageCommand()).build());
		 * 
		 * Not useful here
		 * 
		 * children.put(Lists.newArrayList("r", "reply"),
		 * CommandSpec.builder().permission("ray.message").description(Text.of(
		 * "Reply to a player."))
		 * .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")
		 * )) .executor(new ReplyCommand()).build());
		 */
	}

	public static Map<List<String>, ? extends CommandCallable> getChildren() {
		return children;
	}

	public static CommandExecutor getExecutor() {
		return new RayCommand();
	}
}
