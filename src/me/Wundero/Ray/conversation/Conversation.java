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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;

//bukkit conversation api but not built in and slightly simpler
public abstract class Conversation {
	private Prompt currentPrompt;
	private ConversationListener listener;
	private ConversationContext context;
	private boolean suppressMessages, echoInputs, started = false;
	private List<ConversationCanceller> cancellers = Utils.sl();
	private Text prefix = Text.of();

	public boolean start() {
		if (Sponge.getEventManager().post(new ConversationEvent.Start(
				Cause.source(context.getPlugin()).named(NamedCause.of("conversation", this)).build(), context))) {
			return false;
		}
		started = true;
		context.setOriginal(context.getHolder().getMessageChannel());
		MessageChannel channelNew = context.getOriginal();
		if (suppressMessages) {
			channelNew = MessageChannel.TO_NONE;
		}
		context.getHolder().setMessageChannel(channelNew);
		context.getHolder().sendMessage(prefix.concat(currentPrompt.getQuestion(context)));
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), listener);
		context.putData("conversation", this);
		return true;
	}

	public boolean next(Prompt next) {
		if (next == null) {
			return finish();
		}
		if (Sponge.getEventManager()
				.post(new ConversationEvent.Next(
						Cause.source(context.getPlugin()).named(NamedCause.of("conversation", this)).build(), context,
						next, currentPrompt))) {
			return false;
		}
		currentPrompt = next;
		context.getHolder().sendMessage(prefix.concat(currentPrompt.getQuestion(context)));
		return true;
	}

	public boolean cancel(Optional<ConversationCanceller> canceller) {
		if (Sponge.getEventManager()
				.post(new ConversationEvent.Cancel(
						Cause.source(context.getPlugin()).named(NamedCause.of("conversation", this)).build(), context,
						canceller))) {
			return false;
		}
		Sponge.getEventManager().unregisterListeners(listener);
		context.getHolder().setMessageChannel(context.getOriginal());
		started = false;
		return true;
	}

	public boolean finish() {
		if (Sponge.getEventManager().post(new ConversationEvent.Finish(
				Cause.source(context.getPlugin()).named(NamedCause.of("conversation", this)).build(), context))) {
			return false;
		}
		Sponge.getEventManager().unregisterListeners(listener);
		context.getHolder().setMessageChannel(context.getOriginal());
		started = false;
		return true;
	}

	/**
	 * @return the currentPrompt
	 */
	public Prompt getCurrentPrompt() {
		return currentPrompt;
	}

	/**
	 * @param currentPrompt
	 *            the currentPrompt to set
	 */
	public void setCurrentPrompt(Prompt currentPrompt) {
		this.currentPrompt = currentPrompt;
	}

	/**
	 * @return the listener
	 */
	public ConversationListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(ConversationListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the context
	 */
	public ConversationContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(ConversationContext context) {
		this.context = context;
	}

	/**
	 * @return the suppressMessages
	 */
	public boolean isSuppressMessages() {
		return suppressMessages;
	}

	/**
	 * @param suppressMessages
	 *            the suppressMessages to set
	 */
	public void setSuppressMessages(boolean suppressMessages) {
		this.suppressMessages = suppressMessages;
	}

	/**
	 * @return the echoInputs
	 */
	public boolean isEchoInputs() {
		return echoInputs;
	}

	/**
	 * @param echoInputs
	 *            the echoInputs to set
	 */
	public void setEchoInputs(boolean echoInputs) {
		this.echoInputs = echoInputs;
	}

	/**
	 * @return the cancellers
	 */
	public List<ConversationCanceller> getCancellers() {
		return cancellers;
	}

	/**
	 * @param cancellers
	 *            the cancellers to set
	 */
	public void setCancellers(List<ConversationCanceller> cancellers) {
		this.cancellers = cancellers;
	}

	public void removeCanceller(ConversationCanceller canceller) {
		this.cancellers.remove(canceller);
	}

	public void addCanceller(ConversationCanceller canceller) {
		this.cancellers.add(canceller);
	}

	/**
	 * @return the started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * @return the prefix
	 */
	public Text getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(Text prefix) {
		this.prefix = prefix;
	}
}
