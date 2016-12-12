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

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Listener built around conversations - handles events
 */
public abstract class ConversationListener {

	// allow custom handles on event calls

	private Conversation conversation;

	/**
	 * Create a conversation listener for a builder - conversation will be
	 * registered on build
	 */
	public ConversationListener() {
	}

	/**
	 * Create a listener with a conversation in it
	 */
	public ConversationListener(Conversation convo) {
		this.conversation = convo;
	}

	/**
	 * Listen to chat messages
	 */
	public abstract void onChat(ConversationEvent.Chat chat);

	/**
	 * Listen to finishing the conversation
	 */
	public abstract void onFinish(ConversationEvent.Finish finish);

	/**
	 * Listen to conversation cancelling
	 */
	public abstract void onCancel(ConversationEvent.Cancel cancel);

	/**
	 * Listen to when the conversation changes prompts
	 */
	public abstract void onNext(ConversationEvent.Next next);

	/**
	 * Listen to when the conversation starts
	 */
	public abstract void onStart(ConversationEvent.Start start);

	// private event calls to abstract - had to do this because annotations
	// cannot be forced.

	@Listener
	public final void f(ConversationEvent.Finish c) {
		onFinish(c);
	}

	@Listener
	public final void ch(ConversationEvent.Chat c) {
		onChat(c);
	}

	@Listener
	public final void ca(ConversationEvent.Cancel c) {
		onCancel(c);
	}

	@Listener
	public final void s(ConversationEvent.Start c) {
		onStart(c);
	}

	@Listener
	public final void n(ConversationEvent.Next c) {
		onNext(c);
	}

	// TODO when incoming/outgoing chat messages are event based, cancel all
	// msgs going to player/queue them

	// handles conversing chat inputs

	/**
	 * Handle incoming chat messages
	 */
	@Listener
	public final void internalchat(MessageChannelEvent.Chat event) {
		if (!conversation.isStarted()) {
			return;
		}
		Player p = null;
		if (event.getCause().containsType(Player.class)) {
			p = (Player) event.getCause().first(Player.class).get();
		} else {
			if (!conversation.isSuppressMessages()) {
				return;
			}
			event.getChannel().ifPresent(channel -> {
				MutableMessageChannel cj = channel.asMutable();
				cj.removeMember(conversation.getContext().getHolder());
				event.setChannel(cj);
			});
			return;
		}
		if (conversation.getContext().getHolder().getUniqueId().equals(p.getUniqueId())) {
			event.setCancelled(true);
			ConversationContext context = conversation.getContext();
			String input = event.getRawMessage().toPlain();
			ConversationEvent.Chat event1 = new ConversationEvent.Chat(
					Cause.source(context.getPlugin()).named(NamedCause.of("conversation", this)).build(), context,
					input);
			if (!Sponge.getEventManager().post(event1)) {
				if (conversation.isEchoInputs()) {
					p.sendMessage(conversation.getPrefix().concat(
							Text.of(TextColors.RESET).concat(TextSerializers.FORMATTING_CODE.deserialize(input))));
				}
				for (ConversationCanceller c : conversation.getCancellers()) {
					if (c.checkCancel(conversation, input)) {
						return;
					}
				}
				Prompt pr = conversation.getCurrentPrompt().input(context, input);
				conversation.next(pr);
			}
		} else {
			if (!conversation.isSuppressMessages()) {
				return;
			}
			event.getChannel().ifPresent(channel -> {
				MutableMessageChannel cj = channel.asMutable();
				cj.removeMember(conversation.getContext().getHolder());
				event.setChannel(cj);
			});
			return;
		}
	}

	/**
	 * Update player reference on death
	 */
	@Listener(order = Order.POST)
	public final void internaldeath(DestructEntityEvent.Death event) {
		if (event.getTargetEntity() instanceof Player) {
			Player p = (Player) event.getTargetEntity();
			if (p.getUniqueId().equals(conversation.getContext().getHolderID())) {
				conversation.getContext().updateHolder();
			}
		}
	}

	// ends conversations safely
	/**
	 * Safe conversation cancel on quit
	 */
	@Listener
	public final void internalquit(ClientConnectionEvent.Disconnect event) {
		if (!conversation.isStarted()) {
			return;
		}
		Player p = null;
		if (event.getCause().containsType(Player.class)) {
			p = (Player) event.getCause().first(Player.class).get();
		} else {
			return;
		}
		if ((conversation.getContext().getHolder().getUniqueId().equals(p.getUniqueId()))) {
			conversation.cancel(Optional.empty());
		}
	}

	final void setConversation(Conversation convo) {
		this.conversation = convo;
	}

	/**
	 * Get the conversation
	 */
	public final Conversation getConversation() {
		return conversation;
	}
}
