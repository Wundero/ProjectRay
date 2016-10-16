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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.translation.TranslationFile;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Format type that translates a set key into a string value. [UNTESTED]
 */
public class TranslatableFormat extends Format {

	private String key;

	public TranslatableFormat(ConfigurationNode node) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		key = node.getNode("key").getString();
	}

	@Override
	public boolean send(MessageReceiver f, Map<String, Object> args, Optional<Object> o) {
		TextTemplate template = TextUtils.parse(TranslationFile.getTranslation(key).get(), true);
		return this.s(f, args, template, o);
	}

	@Override
	public boolean send(MessageReceiver f, ParsableData data, Optional<Object> o) {
		TextTemplate template = null;
		if (data.getObserver().isPresent()) {
			template = TextUtils.parse(TranslationFile.getTranslation(key).get(data.getObserver().get().getLocale()),
					true);
		} else if (data.getRecipient().isPresent()) {
			template = TextUtils.parse(TranslationFile.getTranslation(key).get(data.getRecipient().get().getLocale()),
					true);
		} else if (data.getSender().isPresent()) {
			template = TextUtils.parse(TranslationFile.getTranslation(key).get(data.getSender().get().getLocale()),
					true);
		} else {
			template = TextUtils.parse(TranslationFile.getTranslation(key).get(), true);
		}
		return this.s(f, data, template, o);
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new Prompt(TextTemplate.of("What would you like to set as the key?")) {

			@Override
			public Text getQuestion(ConversationContext context) {
				return formatTemplate(context);
			}

			@Override
			public Optional<List<Option>> options(ConversationContext context) {
				return Optional.empty();
			}

			@Override
			public Text getFailedText(ConversationContext context, String failedInput) {
				return Text.of("Yikes! Something went wrong.");
			}

			@Override
			public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
				ConfigurationNode node = context.getData("node");
				node.getNode("key").setValue(text);
				context.sendMessage(Text.of(TextColors.GREEN, "Key set!"));
				return returnTo;
			}
		};
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		return false;
	}

	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		return Optional.empty();
	}

}
