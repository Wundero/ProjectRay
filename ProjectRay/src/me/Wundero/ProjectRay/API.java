package me.Wundero.ProjectRay;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import me.Wundero.ProjectRay.fanciful.FancyMessage;

import org.bukkit.entity.Player;

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
public class API {

	public API() {
	}

	/**
	 * @param to
	 *            Player(s) to send message to, null to broadcast
	 * @param message
	 *            Message to send to player(s)
	 */
	public void sendJSONMessage(final FancyMessage message, final Player... to) {
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
	 * TRY TO AVOID USING THIS METHOD AS IT CAN CAUSE ERRORS IF THE JSON IS NOT
	 * PROPERLY FORMATTED. USE FANCYMESSAGES AND THE
	 * sendJSONMessage/broadcastJSONMessage METHODS TO SEND JSON
	 * */
	public void sendjson(final String json, final Player... to) {

		PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
		String j = (json);
		chat.getChatComponents().write(0, WrappedChatComponent.fromJson(j));
		jsons.add(chat.getChatComponents().read(0).getJson().toLowerCase());
		if ((to == null) || (to.length == 0)) {
			ProtocolLibrary.getProtocolManager().broadcastServerPacket(chat);
			return;
		}
		for (Player p : to) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, chat);
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
