package me.Wundero.Ray.framework.channel;
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

import java.util.function.Consumer;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Default channel configurations
 */
public class DefaultChannels {

	/**
	 * Loader enum
	 */
	public static enum DefaultChannel {
		SIMPLE(c -> simple(c)), ADVANCED(c -> advanced(c)), LEVELED(c -> leveled(c));

		private Consumer<ConfigurationNode> c;

		DefaultChannel(Consumer<ConfigurationNode> c) {
			this.c = c;
		}

		public void a(ConfigurationNode n) {
			c.accept(n);
		}

	}

	/**
	 * Load channels into a configuration file.
	 */
	public static void applyChannels(ConfigurationNode node, DefaultChannel setup) {
		setup.a(node);
	}

	private static void simple(ConfigurationNode node) {
		ConfigurationNode g = node.getNode("global");
		g.getNode("name").setValue("global");
		g.getNode("autojoin").setValue(true);
		g.getNode("tag").setValue("&0[&2G&0]");
	}

	private static void advanced(ConfigurationNode node) {
		ConfigurationNode global = node.getNode("global"), local = node.getNode("local"), mod = node.getNode("mod");
		global.getNode("name").setValue("global");
		global.getNode("autojoin").setValue(true);
		global.getNode("tag").setValue("&0[&2G&0]");
		local.getNode("name").setValue("local");
		local.getNode("autojoin").setValue(true);
		local.getNode("tag").setValue("&0[&aL&0]");
		local.getNode("range").setValue(250.0D);
		mod.getNode("name").setValue("mod");
		mod.getNode("autojoin").setValue(false);
		mod.getNode("permission").setValue("ray.channel.mod");
		mod.getNode("tag").setValue("&0[&cMod&0]");
	}

	private static void leveled(ConfigurationNode node) {
		ConfigurationNode whisper = node.getNode("whisper"), talk = node.getNode("talk"), shout = node.getNode("shout");
		whisper.getNode("name").setValue("whisper");
		whisper.getNode("autojoin").setValue(true);
		whisper.getNode("tag").setValue("&0[&7W&0]");
		whisper.getNode("range").setValue(50.0D);
		talk.getNode("name").setValue("talk");
		talk.getNode("autojoin").setValue(true);
		talk.getNode("tag").setValue("&0[&fT&0]");
		talk.getNode("range").setValue(500.0D);
		shout.getNode("name").setValue("shout");
		shout.getNode("autojoin").setValue(true);
		shout.getNode("tag").setValue("&0[&cS&0]");
	}

}
