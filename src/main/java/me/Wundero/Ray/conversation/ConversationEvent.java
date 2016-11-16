package me.Wundero.Ray.conversation;

import java.util.Optional;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

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

/**
 * Event class called whenever something happens in a conversation
 */
public abstract class ConversationEvent extends AbstractEvent implements Cancellable {

	private Cause cause;
	private ConversationContext context;
	private boolean cancelled = false;

	/**
	 * Create a new event
	 */
	public ConversationEvent(Cause cause, ConversationContext context) {
		this.cause = cause;
		this.setContext(context);
	}

	/**
	 * @return the cause of the event
	 */
	@Override
	public Cause getCause() {
		return cause;
	}

	/**
	 * @return the context of the conversation
	 */
	public ConversationContext getContext() {
		return context;
	}

	/**
	 * Set the context of the conversation
	 */
	public void setContext(ConversationContext context) {
		this.context = context;
	}

	/**
	 * @return whether this event is cancelled or not
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set this event as cancelled
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	/**
	 * Event called when the conversation starts
	 */
	public static class Start extends ConversationEvent {

		public Start(Cause cause, ConversationContext context) {
			super(cause, context);
		}

	}

	/**
	 * Event called when the conversation is cancelled
	 */
	public static class Cancel extends ConversationEvent {

		private Optional<ConversationCanceller> canceller;

		public Cancel(Cause cause, ConversationContext context, Optional<ConversationCanceller> canceller) {
			super(cause, context);
			this.setCanceller(canceller);
		}

		/**
		 * @return the canceller which cancelled the conversation
		 */
		public Optional<ConversationCanceller> getCanceller() {
			return canceller;
		}

		/**
		 * Set the canceller which cancelled the conversation
		 */
		public void setCanceller(Optional<ConversationCanceller> canceller) {
			this.canceller = canceller;
		}

	}

	/**
	 * Event called when the conversation finishes.
	 */
	public static class Finish extends ConversationEvent {

		public Finish(Cause cause, ConversationContext context) {
			super(cause, context);
		}

	}

	/**
	 * Event called when input is handled and the next prompt is given
	 */
	public static class Next extends ConversationEvent {

		private Prompt next, current;

		public Next(Cause cause, ConversationContext context, Prompt next, Prompt current) {
			super(cause, context);
			this.setNext(next);
		}

		/**
		 * @return the prompt which will be sent next
		 */
		public Prompt getNext() {
			return next;
		}

		/**
		 * Set the prompt to handle next
		 */
		public void setNext(Prompt next) {
			this.next = next;
		}

		/**
		 * The prompt that just parsed input
		 */
		public Prompt getCurrent() {
			return current;
		}

		/**
		 * Set the prompt which just parsed input
		 */
		public void setCurrent(Prompt current) {
			this.current = current;
		}

	}

	/**
	 * Event called when the holder puts something into chat
	 */
	public static class Chat extends ConversationEvent {

		private String input;

		public Chat(Cause cause, ConversationContext context, String input) {
			super(cause, context);
			this.setInput(input);
		}

		/**
		 * @return the message sent to the conversation
		 */
		public String getInput() {
			return input;
		}

		/**
		 * Set the message sent to the conversation
		 */
		public void setInput(String input) {
			this.input = input;
		}

	}

}
