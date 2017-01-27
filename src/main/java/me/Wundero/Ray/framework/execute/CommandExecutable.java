package me.Wundero.Ray.framework.execute;
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
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandExecutable implements Executable {

	@Setting
	private List<Cmd> commands;

	@ConfigSerializable
	public static class Cmd {
		@Setting("use-console")
		private boolean isConsole = false;
		@Setting
		private String command;
		// TODO more
	}

	@Override
	public Optional<Text> send(MessageReceiver r) {
		if (!(r instanceof CommandSource)) {
			return Optional.empty();
		}
		CommandSource src = (CommandSource) r;
		boolean ic = Sponge.getServer().getConsole().equals(src);
		for (Cmd c : commands) {
			if (c.isConsole && ic || !c.isConsole && !ic) {
				Sponge.getCommandManager().process(src, c.command);
			}
		}
		return Optional.empty();
	}

}
