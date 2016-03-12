package me.Wundero.ProjectRay;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

import me.Wundero.ProjectRay.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;

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
public class ProjectRay extends JavaPlugin implements IRay {
	private static ProjectRay instance;

	public static ProjectRay get() {
		return instance;
	}

	public void log(String s) {
		log(s, Level.INFO);
	}

	public void log(String s, Level l) {
		Bukkit.getLogger().log(l, "ProjectRay > " + s);
	}

	@Override
	public void onEnable() {
		instance = this;
		// TODO load hooks
		// TODO config
		// TODO command architecture - potentially use GUI's?
		// TODO language support
		// TODO put liscence in all files
		// TODO multiple server type support - sponge & spigot
		// TODO better GUI system - usefull for select sects
		// TODO converter from other plugins
		// TODO customizable name
		// TODO try to minimize singletons (static = overhead)
		// TODO try to minimize reflection (overhead)
		// TODO performance monitor (this plugin only, don't do massivelag/top)
		// TODO event structure and animation handler?
		// TODO credit mkremins for fanciful lib - state that is modified???

		// IDEAS:
		/*
		 * Custom commands like DeluxeCommands
		 * 
		 * Control all text not just chat - bossbar/scoreboard/tablist
		 * name&header&footer/actionbar/title/subtitle/whatever else
		 * 
		 * perhaps shift to multi purpose plugin: permissions plugin w/ group &
		 * player architecture command struct like ess/pex/others all in one
		 * 
		 * better scripting system - support lua, js and other script langs
		 * 
		 * config helper - ext program that allows you to format chat how you
		 * want and gens a config based on the settings you choose - support
		 * json by selecting sections and adding click/hover to them
		 * 
		 * in-game file browser - use GUI's and fancy-convos to edit files
		 * nicely - support txt yaml and other files
		 * 
		 * animations - figure out best way to do this (threads, events, etc.)
		 * 
		 * figure out best way to have changing text in-game w/o resending text,
		 * or best way of resending text - when dc2 release decomp to look for
		 * this
		 * 
		 * minimize dependencies (try to remove protocollib and vault (because
		 * can be perms plugin) and add as soft-depends)
		 * 
		 * mavenize (add repo, remove libs)
		 * 
		 * try to increase version support (1.7-1.9 at least, 1.4-1.9 best)
		 * 
		 * converter from fanciful to texts (spigot to sponge)
		 */
	}

	@Override
	public void onDisable() {

	}

	private API api;

	public API getAPI() {
		return api;
	}

	public static class API {
		/**
		 * @param to
		 *            Player(s) to send message to, null to broadcast
		 * @param message
		 *            Message to send to player(s)
		 */
		public void sendJSONMessage(final FancyMessage message,
				final Player... to) {
			try {
				sendjson(message.clone().toJSONString(), to);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		// local cache of sent messages so if packet is intercepted by this
		// plugin it can ignore it
		public static ArrayList<String> jsons = Lists.newArrayList();

		/**
		 * TRY TO AVOID USING THIS METHOD AS IT CAN CAUSE ERRORS IF THE JSON IS
		 * NOT PROPERLY FORMATTED. USE FANCYMESSAGES AND THE
		 * sendJSONMessage/broadcastJSONMessage METHODS TO SEND JSON
		 * */
		public void sendjson(final String json, final Player... to) {

			PacketContainer chat = new PacketContainer(
					PacketType.Play.Server.CHAT);
			String j = (json);
			chat.getChatComponents().write(0, WrappedChatComponent.fromJson(j));
			jsons.add(chat.getChatComponents().read(0).getJson().toLowerCase());
			if ((to == null) || (to.length == 0)) {
				ProtocolLibrary.getProtocolManager()
						.broadcastServerPacket(chat);
				return;
			}
			for (Player p : to) {
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(p,
							chat);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * @param message
		 *            Message to broadcast
		 */
		public void broadcastJSONMessage(final FancyMessage message) {
			sendJSONMessage(message);
		}
	}

	@Override
	public File getDirectory() {
		return this.getDataFolder();
	}

	@Override
	public String getVersion() {
		return this.getDescription().getVersion();
	}
}
