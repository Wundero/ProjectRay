package me.Wundero.ProjectRay.framework.format;
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

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.conversation.BooleanPrompt;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class MultiFormat extends Format {

	private List<Format> formats = Utils.sl();
	private Mode mode = Mode.SHUFFLE;
	private Random r = new Random();
	private ArrayDeque<Format> unused;

	private synchronized Format getNext(boolean pop) {
		switch (mode) {
		case SHUFFLE:
		case SEQUENCE:
			if (unused.isEmpty()) {
				populateDeque();
			}
			return pop ? unused.pop() : unused.peek();
		default:
			return formats.get(r.nextInt(formats.size()));
		}
	}

	private void populateDeque() {
		if (mode == Mode.SHUFFLE) {
			formats = Utils.scramble(formats);
		}
		unused = formats.stream().collect(Collectors.toCollection(ArrayDeque::new));
	}

	private enum Mode {
		SEQUENCE, RANDOM, SHUFFLE;

		public static Mode getMode(String m) {
			if (m == null) {
				return SHUFFLE;
			}
			m = m.toLowerCase().trim();
			if ("random".startsWith(m)) {
				return RANDOM;
			}
			if ("sequence".startsWith(m)) {
				return SEQUENCE;
			}
			return SHUFFLE;
		}
	}

	public MultiFormat(ConfigurationNode node) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		mode = Mode.getMode(node.getNode("mode").getString("shuffle"));
		ConfigurationNode subs = node.getNode("formats");
		for (ConfigurationNode f : subs.getChildrenMap().values()) {
			formats.add(Format.create(f));
		}
		populateDeque();
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		return getNext(true).send(f, args);
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		return getNext(true).send(f, data);
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
			node = node.getNode("formats", text);
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

	private static class OrderPrompt extends Prompt {

		private Prompt r;

		public OrderPrompt(Prompt r) {
			this(TextTemplate.of(TextColors.AQUA, "What order would you like the formats to be chosen in? ",
					TextTemplate.arg("options").build()));
			this.r = r;
		}

		public OrderPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return this.formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> list = Utils.sl();
			list.add(Option.build("sequence", "sequence"));
			list.add(Option.build("shuffle", "shuffle"));
			list.add(Option.build("random", "random"));
			return Optional.of(list);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid order!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			node.getNode("mode").setValue(selected.get().getValue().toString());
			return new NamePrompt(r);
		}

	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new OrderPrompt(returnTo);
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz) {
		Format f = getNext(false);
		if (f.getClass().equals(clazz)) {
			return true;
		}
		return f.hasInternal(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz) {
		if (!hasInternal(clazz)) {
			return Optional.empty();
		}
		Format f = getNext(false);
		if (f.getClass().equals(clazz)) {
			return Optional.of((T) f);
		}
		return f.getInternal(clazz);
	}
}
