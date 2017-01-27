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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.conversation.BooleanPrompt;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;

/**
 * Format type that holds multiple subformats, and selects based on a selection
 * mode which to use.
 */
public class MultiFormat extends Format {

	@Setting
	private List<Format> formats = Utils.sl();
	@Setting
	private Mode mode = Mode.SHUFFLE;
	private Random r = new Random();
	private Deque<Format> unused = Utils.ad();

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
	}

	@Override
	public boolean send(MessageReceiver f, Map<String, Object> args, Optional<Object> o, Optional<UUID> u, boolean b) {
		return getNext(true).send(f, args, o, u, b);
	}

	@Override
	public boolean send(MessageReceiver f, ParsableData data, Optional<Object> o, Optional<UUID> u, boolean b) {
		return getNext(true).send(f, data, o, u, b);
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
			ConfigurationNode node = context.getData("node", ConfigurationNode.class, null);
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
			List<Option> list = Utils.al();
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
			ConfigurationNode node = context.getData("node", ConfigurationNode.class, null);
			node.getNode("mode").setValue(selected.get().getValue().toString());
			return new NamePrompt(r);
		}

	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new OrderPrompt(returnTo);
	}

	@Override
	public void applyRootInt(String name, ConfigurationNode root) {
		populateDeque();
		for (Format f : formats) {
			f.setOwner(this);
		}
	}

}
