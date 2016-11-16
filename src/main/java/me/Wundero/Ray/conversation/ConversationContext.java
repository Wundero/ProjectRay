package me.Wundero.Ray.conversation;
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

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import me.Wundero.Ray.DataHolder;

/**
 * Context represents the data the conversation knows, and is persistent through
 * prompts.
 */
public class ConversationContext extends DataHolder {
	// data for conversation
	private Player holder;
	private UUID holderID;
	private Object plugin;
	private MessageChannel original;

	/**
	 * Create a new context for a conversation
	 */
	ConversationContext(Object plugin, Player holder) {
		this.setPlugin(plugin);
		this.setHolder(holder);
	}

	/**
	 * Send a message (with prefix) to the holder
	 */
	public void sendMessage(Text message) {
		Conversation convo = getData("conversation");
		Text prefix = convo.getPrefix();
		holder.sendMessage(prefix.concat(message));
	}

	/**
	 * @return the conversation holder
	 */
	public Player getHolder() {
		return holder;
	}

	private void setHolder(Player holder) {
		this.holder = holder;
		this.setHolderID(holder.getUniqueId());
	}

	/**
	 * Update player reference object
	 */
	public void updateHolder() {
		Player p = Sponge.getServer().getPlayer(holderID).get();
		setHolder(p);
	}

	/**
	 * @return the plugin that started the conversation
	 */
	public Object getPlugin() {
		return plugin;
	}

	private void setPlugin(Object plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return the original message channel
	 */
	public MessageChannel getOriginal() {
		return original;
	}

	/**
	 * Set the original message channel - the holder's channel will be set to
	 * this on finish
	 */
	public void setOriginal(MessageChannel original) {
		this.original = original;
	}

	/**
	 * @return the holderID
	 */
	public UUID getHolderID() {
		return holderID;
	}

	private void setHolderID(UUID holderID) {
		this.holderID = holderID;
	}
}
