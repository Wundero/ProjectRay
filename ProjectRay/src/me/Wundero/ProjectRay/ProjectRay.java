package me.Wundero.ProjectRay;

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

public class ProjectRay extends JavaPlugin {
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
		// TODO command architecture
		// TODO language support
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
}
