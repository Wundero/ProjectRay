package me.Wundero.Ray.framework.format;
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

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.channel.MessageReceiver;

import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class EffectFormat extends Format {

	public EffectFormat(ConfigurationNode node) {
		super(node);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> sender) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender) {
		// TODO Auto-generated method stub
		return false;
	}

}
