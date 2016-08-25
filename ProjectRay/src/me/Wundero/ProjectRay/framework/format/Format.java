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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Format {
	private FormatType type;
	private String name;
	protected boolean usable = false;
	private Optional<ConfigurationNode> node;

	public abstract Prompt getConversationBuilder(Prompt returnTo, ConversationContext context);

	public static Prompt buildConversation(Prompt p, ConversationContext c, ConfigurationNode newNode) {
		return;//not letting this build compile as this is unfinished
	}

	public abstract boolean send(Function<Text, Boolean> f, Map<String, Object> args);

	public abstract boolean send(Function<Text, Boolean> f, ParsableData data);

	protected boolean s(Function<Text, Boolean> f, Map<String, Object> a, TextTemplate t) {
		Function<Text, Boolean> f2 = (text) -> {
			Text t2 = text;
			try {
				t2 = Utils.parse(text, new ParsableData().setKnown(a), Optional.of(this));
			} catch (Exception e) {
				return f.apply(text);
			}
			return f.apply(t2);
		};
		boolean b = false;
		try {
			b = f2.apply(get(t, a));
		} catch (Exception e) {
			b = false;
		}
		return b;
	}

	protected boolean s(Function<Text, Boolean> f, ParsableData d, TextTemplate t) {
		Function<Text, Boolean> f2 = (text) -> {
			Text t2 = text;
			try {
				t2 = Utils.parse(text, d, Optional.of(this));
			} catch (Exception e) {
				return f.apply(text);
			}
			return f.apply(t2);
		};
		boolean b = false;
		try {
			b = f2.apply(get(t, d));
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}

	protected Text get(TextTemplate t, Map<String, Object> data) {
		return t.apply(data).build();
	}

	protected Text get(TextTemplate t, ParsableData data) {
		return t.apply(parse(t, data)).build();
	}

	protected Map<String, Object> parse(TextTemplate t, ParsableData data) {
		if (data == null) {
			return Utils.sm();
		}
		return Ray.get().setVars(data.getKnown().orElse(Utils.sm()), t, data.getSender(), data.getRecipient(),
				data.getObserver(), Optional.of(this), data.isClickHover());
	}

	public String getName() {
		return name;
	}

	public Format setName(String name) {
		this.name = name;
		return this;
	}

	public static Format create(ConfigurationNode node) {
		if (node == null || node.isVirtual()) {
			return null;
		}
		boolean event = !node.getNode("event").isVirtual();
		if (!node.getNode("frames").isVirtual()) {
			return event ? new EventFormat(node, new AnimatedFormat(node)) : new AnimatedFormat(node);
		}
		if (!node.getNode("formats").isVirtual()) {
			return event ? new EventFormat(node, new MultiFormat(node)) : new MultiFormat(node);
		}
		if (!node.getNode("key").isVirtual()) {
			return event ? new EventFormat(node, new TranslatableFormat(node)) : new TranslatableFormat(node);
		}
		return event ? new EventFormat(node, new StaticFormat(node)) : new StaticFormat(node);
	}

	public Format(final ConfigurationNode node) {
		this.setNode(Optional.ofNullable(node));
		if (node == null || node.isVirtual()) {
			return;
		}
		name = node.getKey().toString();
		setType(node.getNode("type").isVirtual() ? FormatType.fromString(name)
				: FormatType.fromString(node.getNode("type").getString()));
		node.getNode("type").setValue(type.getName());
		// forces type to be present
	}

	public boolean usable() {
		return usable;
	}

	public FormatType getType() {
		return type;
	}

	public Format setType(FormatType type) {
		this.type = type;
		return this;
	}

	public Optional<ConfigurationNode> getNode() {
		return node;
	}

	public void setNode(Optional<ConfigurationNode> node) {
		this.node = node;
	}

	public static Format valueOf(String arg) {
		switch (arg.toLowerCase().trim()) {
		case "animated":
		case "anim":
		case "animate":
		case "a":
			return af;
		case "event":
		case "e":
			return ef;
		case "multi":
		case "many":
		case "m":
			return mf;
		case "translatable":
		case "translate":
		case "t":
			return tf;
		}
		return sf;
	}

	private static class ShouldAnimatePrompt extends Prompt {

		private Prompt p;

		public ShouldAnimatePrompt(Prompt p) {
			this(TextTemplate.of(TextColors.AQUA, "Do you want to animate this format?"));
			this.p = p;
		}

		public ShouldAnimatePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			switch (input.toLowerCase().trim()) {
			case "y":
			case "yes":
			case "n":
			case "no":
			case "true":
			case "t":
			case "f":
			case "false":
			case "0":
			case "1":
				return true;
			default:
				return false;
			}
		}

		private boolean parseInput(String input) {
			switch (input.toLowerCase().trim()) {
			case "y":
			case "yes":
			case "true":
			case "t":
			case "1":
				return true;
			default:
				return false;
			}
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return this.formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			return Optional.empty();
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid input! Try yes or no.");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (parseInput(text)) {
				context.putData("framenumber", 0);
				context.putData("animated", true);
				ConfigurationNode n = context.getData("node");
				context.putData("frame0", n.getNode("frames", "frame0"));
				return Format.buildConversation(af.getConversationBuilder(p, context), context,
						n.getNode("frames", "frame0"));
			} else {
				return p;
			}
		}
	}

	private static AnimatedFormat af = new AnimatedFormat(null);
	private static StaticFormat sf = new StaticFormat(null);
	private static EventFormat ef = new EventFormat(null, null);
	private static MultiFormat mf = new MultiFormat(null);
	private static TranslatableFormat tf = new TranslatableFormat(null);
}
