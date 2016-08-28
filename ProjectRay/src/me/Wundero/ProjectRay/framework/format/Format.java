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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.conversation.TypePrompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Format {
	private FormatType type;
	private String name;
	protected boolean usable = false;
	private Optional<ConfigurationNode> node;

	public abstract Prompt getConversationBuilder(Prompt returnTo, ConversationContext context);

	public static Prompt buildConversation(Prompt p, final ConversationContext c, final ConfigurationNode newNode) {
		ConfigurationNode oldNode = c.getData("node");
		oldNode.getNode("type").setValue(((FormatType) c.getData("formattype")).getName());
		newNode.getNode("type").setValue(((FormatType) c.getData("formattype")).getName());
		c.putData("node", newNode);
		return new FormatPrompt(p == null ? null : new WrapperPrompt(p, oldNode, c));
	}

	private static class WrapperPrompt extends Prompt {

		private Runnable r;

		public WrapperPrompt(Prompt p, final ConfigurationNode n, final ConversationContext c) {
			this(p.getTemplate());
			this.p = p;
			r = () -> c.putData("node", n);
		}

		public WrapperPrompt(TextTemplate template) {
			super(template);
		}

		private Prompt p;

		@Override
		public Text getQuestion(ConversationContext context) {
			r.run();
			return p.getQuestion(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			return p.options(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return p.getFailedText(context, failedInput);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			return p.onInput(selected, text, context);
		}
	}

	private static class FormatPrompt extends TypePrompt<Format> {

		private Prompt returnTo;

		public FormatPrompt(Prompt returnTo) {
			this(TextTemplate.of("What type of format would you like to make (you can type \"done\" to exit)? ",
					get("animated", "event", "multi", "translatable", "static")), Optional.of(Format.class));
			this.returnTo = returnTo;
		}

		private static Text get(String... types) {
			Text out = Text.of(TextColors.GOLD);
			String f = "[%s]";
			TextColor color = TextColors.GOLD;
			String c = "%s";
			String h = "Click here to select %s!";
			TextColor hc = TextColors.AQUA;
			boolean ft = true;
			for (String t : types) {
				if (ft) {
					ft = false;
				} else {
					out = out.concat(Text.of(" "));
				}
				out = out.concat(Text.builder(String.format(f, t)).color(color)
						.onClick(TextActions.runCommand(String.format(c, t)))
						.onHover(TextActions.showText(Text.of(hc, String.format(h, t)))).build());
			}
			return out;
		}

		public FormatPrompt(TextTemplate template, Optional<Class<Format>> type) {
			super(template, type);
		}

		@Override
		public Prompt onTypeInput(Format object, String text, ConversationContext context) {
			return object.getConversationBuilder(returnTo, context);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid format!");
		}

	}

	public abstract boolean send(Function<Text, Boolean> f, Map<String, Object> args);

	public abstract boolean send(Function<Text, Boolean> f, ParsableData data);

	protected boolean s(Function<Text, Boolean> f, Map<String, Object> a, TextTemplate t) {
		System.out.println(t);
		Function<Text, Boolean> f2 = (text) -> {
			Text t2 = text;
			try {
				t2 = Utils.parse(text, new ParsableData().setKnown(a));
				System.out.println(t2);
				if (t2 == null) {
					return f.apply(text);
				}
			} catch (Exception e) {
				return f.apply(text);
			}
			return f.apply(t2);
		};
		boolean b = false;
		Text te = get(t, a);
		try {
			b = f2.apply(te);
		} catch (Exception e) {
			b = false;
		}
		return b;
	}

	protected boolean s(Function<Text, Boolean> f, ParsableData d, TextTemplate t) {
		Function<Text, Boolean> f2 = (text) -> {
			Text t2 = text;
			try {
				t2 = Utils.parse(text, d);
				System.out.println(t2);
				if (t2 == null) {
					return f.apply(text);
				}
			} catch (Exception e) {
				return f.apply(text);
			}
			return f.apply(t2);
		};
		boolean b = false;
		Text te = get(t, d);
		try {
			b = f2.apply(te);
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

	private static AnimatedFormat af = new AnimatedFormat(null);
	private static StaticFormat sf = new StaticFormat(null);
	private static EventFormat ef = new EventFormat(null, null);
	private static MultiFormat mf = new MultiFormat(null);
	private static TranslatableFormat tf = new TranslatableFormat(null);
}
