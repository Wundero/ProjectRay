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

//a class that is called to check if the conversation should cancel on input, and calls when it is cancelled.
public abstract class ConversationCanceller {

	public abstract boolean shouldCancel(ConversationContext context, String input);

	public abstract void onCancel(ConversationContext context);

	// internal method called
	public final boolean checkCancel(Conversation convo, String input) {
		if (shouldCancel(convo.getContext(), input)) {
			onCancel(convo.getContext());
			convo.cancel(Optional.of(this));
			return true;
		}
		return false;
	}

	// standard canceller included
	public static final ConversationCanceller DEFAULT = new ConversationCanceller() {

		@Override
		public boolean shouldCancel(ConversationContext context, String input) {
			return input.toLowerCase().trim().equals("exit");
		}

		@Override
		public void onCancel(ConversationContext context) {
		}

	};
}
