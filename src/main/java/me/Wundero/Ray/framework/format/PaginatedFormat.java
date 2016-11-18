
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
import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;

import me.Wundero.Ray.conversation.BooleanPrompt;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Format type that splits formats into pages [each format is a page].
 */
public class PaginatedFormat extends Format {

	private List<Format> pages = Utils.sl();

	public PaginatedFormat(ConfigurationNode node) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		ConfigurationNode subs = node.getNode("pages");
		for (ConfigurationNode f : subs.getChildrenMap().values()) {
			pages.add(Format.create(f));
		}
	}

	private static class NamePrompt extends Prompt {

		private Prompt r;

		public NamePrompt(Prompt r) {
			this(TextTemplate.of("What would you like to call this format?"));
			this.r = r;
		}

		public NamePrompt(TextTemplate template) {
			super(template);
		}

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
			node = node.getNode("pages", text);
			return Format.buildConversation(new AnotherFormatPrompt(r), context, node);
		}

	}

	private static class AnotherFormatPrompt extends BooleanPrompt {

		public AnotherFormatPrompt(Prompt r) {
			this(TextTemplate.of("Would you like to add another format? ", TextTemplate.arg("options").build()));
			this.r = r;
		}

		private Prompt r;

		public AnotherFormatPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Prompt onBooleanInput(boolean value, String text, ConversationContext context) {
			if (value) {
				return new NamePrompt(r);
			} else {
				return r;
			}
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return Format.buildConversation(new AnotherFormatPrompt(returnTo), context, context.getData("node"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		int i = index.orElse(0);
		if (i < 0 || i >= pages.size()) {
			return Optional.empty();
		}
		Format f = pages.get(i);
		if (f.getClass().equals(clazz)) {
			return (Optional<T>) Utils.wrap(f);
		} else {
			return f.getInternal(clazz, index);
		}
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		return getInternal(clazz, index).isPresent();
	}

	@Override
	public boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> sender, Optional<UUID> u) {
		int i = (Integer) Utils.wrap(args.get("page")).orElse(0);
		if (i < 0 || i >= pages.size()) {
			return false;
		}
		return pages.get(i).send(target, args, sender, u);
	}

	@Override
	public boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender, Optional<UUID> u) {
		int i = data.getPage().orElse(0);
		if (i < 0 || i >= pages.size()) {
			return false;
		}
		return pages.get(i).send(target, data, sender, u);
	}

}
