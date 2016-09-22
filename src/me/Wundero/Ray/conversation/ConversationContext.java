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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import me.Wundero.Ray.DataHolder;

public class ConversationContext extends DataHolder {
	// data for conversation
	private Player holder;
	private Object plugin;
	private MessageChannel original;

	ConversationContext(Object plugin, Player holder) {
		this.setPlugin(plugin);
		this.setHolder(holder);
	}

	public void sendMessage(Text message) {
		Conversation convo = getData("conversation");
		Text prefix = convo.getPrefix();
		holder.sendMessage(prefix.concat(message));
	}

	public Player getHolder() {
		return holder;
	}

	private void setHolder(Player holder) {
		this.holder = holder;
	}

	public Object getPlugin() {
		return plugin;
	}

	private void setPlugin(Object plugin) {
		this.plugin = plugin;
	}

	public MessageChannel getOriginal() {
		return original;
	}

	public void setOriginal(MessageChannel original) {
		this.original = original;
	}
}
