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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.message.MessageChannelEvent;

public abstract class ConversationListener {
	private final Conversation conversation;

	public ConversationListener(Conversation convo) {
		this.conversation = convo;
	}

	@Listener
	public void onChat(ConversationEvent.Chat chat) {

	}

	@Listener
	public void onFinish(ConversationEvent.Finish finish) {

	}

	@Listener
	public void onCancel(ConversationEvent.Cancel cancel) {

	}

	@Listener
	public void onNext(ConversationEvent.Next next) {

	}

	@Listener
	public void onStart(ConversationEvent.Start start) {

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

	public final Conversation getConversation() {
		return conversation;
	}
}
