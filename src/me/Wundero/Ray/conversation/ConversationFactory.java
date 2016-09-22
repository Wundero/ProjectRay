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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import me.Wundero.ProjectRay.conversation.ConversationEvent.Cancel;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Chat;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Finish;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Next;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Start;
import me.Wundero.ProjectRay.utils.Utils;

public class ConversationFactory {

	// builds a conversation for you

	private Prompt firstPrompt;
	private ConversationListener listener = null;
	private ConversationContext context;
	private boolean suppressMessages = true, echoInputs = false;
	private List<ConversationCanceller> cancellers = Utils.sl();
	private Object plugin;
	private Map<String, Object> initialContextdata = Utils.sm();
	private Text prefix = Text.of();

	private ConversationFactory() {
	}

	public Conversation build(Player player) {
		Validate.notNull(firstPrompt);
		Validate.notNull(plugin);
		Validate.notNull(player);
		context = new ConversationContext(plugin, player);
		context.putAll(initialContextdata);
		Conversation convo = new Conversation() {
		};
		if (listener == null) {
			listener = new ConversationListener(convo) {

				@Override
				public void onChat(Chat chat) {
				}

				@Override
				public void onFinish(Finish finish) {
				}

				@Override
				public void onCancel(Cancel cancel) {
				}

				@Override
				public void onNext(Next next) {
				}

				@Override
				public void onStart(Start start) {
				}
			};
		} else {
			listener.setConversation(convo);
		}
		convo.setCancellers(cancellers);
		convo.setContext(context);
		convo.setCurrentPrompt(firstPrompt);
		convo.setEchoInputs(echoInputs);
		convo.setListener(listener);
		convo.setSuppressMessages(suppressMessages);
		convo.setPrefix(prefix);
		return convo;
	}

	public ConversationFactory withPrefix(Text prefix) {
		if (!prefix.toPlain().endsWith(" ")) {
			prefix = prefix.concat(Text.of(" "));
		}
		this.prefix = prefix;
		return this;
	}

	public ConversationFactory withEcho(boolean echoInputs) {
		this.echoInputs = echoInputs;
		return this;
	}

	public ConversationFactory withSuppression(boolean suppress) {
		this.suppressMessages = suppress;
		return this;
	}

	public static ConversationFactory builder(Object plugin) {
		return new ConversationFactory().withPlugin(plugin);
	}

	private ConversationFactory withPlugin(Object plugin) {
		this.plugin = plugin;
		return this;
	}

	public ConversationFactory withFirstPrompt(Prompt prompt) {
		this.firstPrompt = prompt;
		return this;
	}

	public ConversationFactory withListener(ConversationListener listener) {
		this.listener = listener;

		return this;
	}

	public ConversationFactory withInitialContext(Map<String, Object> data) {
		this.initialContextdata = data;
		return this;
	}

	public ConversationFactory withCanceller(ConversationCanceller canceller) {
		cancellers.add(canceller);
		return this;
	}

}
