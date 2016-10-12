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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.conversation.TypePrompt;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.location.FormatLocation;
import me.Wundero.Ray.framework.format.location.FormatLocations;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Format {
	private FormatContext context;
	private String name;
	protected boolean usable = false;
	protected FormatLocation loc = FormatLocations.CHAT;
	private Optional<ConfigurationNode> node;

	@Override
	public boolean equals(Object o) {
		return o instanceof Format && ((Format) o).name.equals(this.name);
	}

	public abstract Prompt getConversationBuilder(Prompt returnTo, ConversationContext context);

	public static Prompt buildConversation(Prompt p, final ConversationContext c, final ConfigurationNode newNode) {
		ConfigurationNode oldNode = c.getData("node");
		oldNode.getNode("context").setValue(((FormatContext) c.getData("formattype")).getName());
		newNode.getNode("context").setValue(((FormatContext) c.getData("formattype")).getName());
		c.putData("node", newNode);
		return new FormatPrompt(p == null ? null : new WrapperPrompt(p, oldNode, c));
	}

	public static Prompt buildConversation(Prompt p, final ConversationContext c, final ConfigurationNode newNode,
			final Format chosen) {
		ConfigurationNode oldNode = c.getData("node");
		oldNode.getNode("context").setValue(((FormatContext) c.getData("formattype")).getName());
		newNode.getNode("context").setValue(((FormatContext) c.getData("formattype")).getName());
		c.putData("node", newNode);
		return chosen.getConversationBuilder(p == null ? null : new WrapperPrompt(p, oldNode, c), c);
	}

	public abstract <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index);

	public abstract boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index);

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
					get("animated", "event", "multi", "translatable", "command", "static", "execute")),
					Optional.of(Format.class));
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

		public FormatPrompt(TextTemplate template, Optional<Class<? extends Format>> type) {
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

	// irrelevant boolean to prevent method signature conflicts

	public boolean send(MessageReceiver target, Map<String, Object> args, Object o, boolean irrelevant) {
		return send(target, args, Optional.ofNullable(o));
	}

	public boolean send(MessageReceiver target, ParsableData data, Object o, boolean irrelevant) {
		return send(target, data, Optional.ofNullable(o));
	}

	public abstract boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> sender);

	public abstract boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender);

	protected boolean s(MessageReceiver target, Map<String, Object> a, TextTemplate t, Optional<Object> sender) {
		Text te = get(t, a);
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	protected boolean s(MessageReceiver target, ParsableData d, TextTemplate t, Optional<Object> sender) {
		Text te = get(t, d);
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	protected boolean s(MessageReceiver target, Map<String, Object> a, Text te, Optional<Object> sender) {
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	protected boolean s(MessageReceiver target, ParsableData d, Text te, Optional<Object> sender) {
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
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
		boolean cmd = !node.getNode("command").isVirtual();
		boolean ex = !node.getNode("commands").isVirtual();
		if (!node.getNode("frames").isVirtual()) {
			return buildFormat(node, new AnimatedFormat(node), event, cmd, ex);
		}
		if (!node.getNode("formats").isVirtual()) {
			return buildFormat(node, new MultiFormat(node), event, cmd, ex);
		}
		if (!node.getNode("key").isVirtual()) {
			return buildFormat(node, new TranslatableFormat(node), event, cmd, ex);
		}
		return buildFormat(node, new StaticFormat(node), event, cmd, ex);
	}

	private static Format buildFormat(ConfigurationNode node, Format towrap, boolean event, boolean command,
			boolean ex) {
		Format out = towrap;
		if (event) {
			return buildFormat(node, new EventFormat(node, towrap), false, command, ex);
		}
		if (command) {
			return buildFormat(node, new CommandFormat(node, towrap), event, false, ex);
		}
		if (ex) {
			return buildFormat(node, new ExecutingFormat(node, towrap), event, command, false);
		}
		return out;
	}

	public Format(final ConfigurationNode node) {
		this.setNode(Optional.ofNullable(node));
		if (node == null || node.isVirtual()) {
			return;
		}
		name = node.getKey().toString();
		setContext(node.getNode("context").isVirtual() ? FormatContext.fromString(name)
				: FormatContext.fromString(node.getNode("context").getString()));
		node.getNode("context").setValue(context.getName());
		setLocation(node.getNode("location").getString("chat"));
		node.getNode("location").setValue(getLocation().getName());
		// forces type to be present
	}

	public void setLocation(FormatLocation loc) {
		this.loc = loc;
	}

	public void setLocation(String name) {
		this.loc = FormatLocations.fromString(name);
	}

	public FormatLocation getLocation() {
		return loc;
	}

	public boolean usable() {
		return usable;
	}

	public FormatContext getContext() {
		return context;
	}

	public Format setContext(FormatContext context) {
		this.context = context;
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
		case "effect":
		case "animated effect":
		case "animatedeffect":
		case "animated-effect":
		case "eff":
			return aef;
		case "executing":
		case "exec":
		case "execute":
		case "x":
			return xf;
		case "multi":
		case "many":
		case "m":
			return mf;
		case "translatable":
		case "translate":
		case "t":
			return tf;
		case "command":
		case "cmd":
		case "c":
			return cf;
		}
		return sf;
	}

	private static ExecutingFormat xf = new ExecutingFormat(null, null);
	private static AnimatedFormat af = new AnimatedFormat(null);
	private static EffectFormat aef = new EffectFormat(null);
	private static StaticFormat sf = new StaticFormat(null);
	private static EventFormat ef = new EventFormat(null, null);
	private static MultiFormat mf = new MultiFormat(null);
	private static TranslatableFormat tf = new TranslatableFormat(null);
	private static CommandFormat cf = new CommandFormat(null, null);
}
