package me.Wundero.ProjectRay.conversation;
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
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class ConversationListener {
	private Conversation conversation;

	public ConversationListener() {
	}

	public ConversationListener(Conversation convo) {
		this.conversation = convo;
	}

	public abstract void onChat(ConversationEvent.Chat chat);

	public abstract void onFinish(ConversationEvent.Finish finish);

	public abstract void onCancel(ConversationEvent.Cancel cancel);

	public abstract void onNext(ConversationEvent.Next next);

	public abstract void onStart(ConversationEvent.Start start);

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

	@Listener
	public final void internalchat(MessageChannelEvent.Chat event) {
		if (!conversation.isStarted()) {
			return;
		}
		Player p = null;
		if (event.getCause().containsType(Player.class)) {
			p = (Player) event.getCause().first(Player.class).get();
		} else {
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
					p.sendMessage(conversation.getPrefix().concat(Text.of(TextColors.RESET, input)));
				}
				for (ConversationCanceller c : conversation.getCancellers()) {
					if (c.checkCancel(conversation, input)) {
						return;
					}
				}
				Prompt pr = conversation.getCurrentPrompt().input(context, input);
				conversation.next(pr);
			}
		}
	}

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

	public final Conversation getConversation() {
		return conversation;
	}
}
