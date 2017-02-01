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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.config.Rootable;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.conversation.TypePrompt;
import me.Wundero.Ray.framework.args.Argument;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.location.FormatLocation;
import me.Wundero.Ray.framework.format.location.FormatLocations;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Represents a sendable object. Generic class that allows players to create
 * dynamic formats depending on need.
 */
@ConfigSerializable
public abstract class Format implements Rootable {
	public static TypeToken<Format> type = TypeToken.of(Format.class);
	@Setting
	private FormatContext context;
	private String name;
	private Format owner;
	protected boolean usable = false;
	@Setting("location")
	protected FormatLocation loc = FormatLocations.CHAT;
	private Optional<ConfigurationNode> node;
	@Setting
	private Map<String, Argument> args = Utils.hm();

	public void addArg(String name, Argument arg) {
		if (name == null || arg == null) {
			return;
		}
		arg.applyRoot(name, null);
		args.put(name, arg);
	}

	/**
	 * Compare formats.
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Format && ((Format) o).name.equals(this.name);
	}

	/**
	 * Create a prompt for building the format.SF
	 */
	public abstract Prompt getConversationBuilder(Prompt returnTo, ConversationContext context);

	/**
	 * Build a conversation with a prompt to return to, context established, and
	 * a node to set the context node to.
	 */
	public static Prompt buildConversation(Prompt p, final ConversationContext c, final ConfigurationNode newNode) {
		ConfigurationNode oldNode = c.getData("node", ConfigurationNode.class, null);
		oldNode.getNode("context").setValue(c.getData("formattype", FormatContext.class, null).getName());
		newNode.getNode("context").setValue(c.getData("formattype", FormatContext.class, null).getName());
		c.putData("node", newNode);
		return new FormatPrompt(p == null ? null : new WrapperPrompt(p, oldNode, c));
	}

	/**
	 * Build a conversation with a prompt to return to, context established, a
	 * format to build from, and a node to set the context node to.
	 */
	public static Prompt buildConversation(Prompt p, final ConversationContext c, final ConfigurationNode newNode,
			final Format chosen) {
		ConfigurationNode oldNode = c.getData("node", ConfigurationNode.class, null);
		oldNode.getNode("context").setValue(c.getData("formattype", FormatContext.class, null).getName());
		newNode.getNode("context").setValue(c.getData("formattype", FormatContext.class, null).getName());
		c.putData("node", newNode);
		return chosen.getConversationBuilder(p == null ? null : new WrapperPrompt(p, oldNode, c), c);
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

	/**
	 * Send a message to the location of this format, parsing for variables.
	 */
	public boolean send(MessageReceiver target, Map<String, Object> args, Object o, UUID uuid, boolean broadcast,
			boolean irrelevant) {
		return send(target, args, Optional.ofNullable(o), Utils.wrap(uuid), broadcast);
	}

	/**
	 * Send a message to the location of this format, parsing for variables.
	 */
	public boolean send(MessageReceiver target, ParsableData data, Object o, UUID uuid, boolean broadcast,
			boolean irrelevant) {
		return send(target, data, Optional.ofNullable(o), Utils.wrap(uuid), broadcast);
	}

	/**
	 * Send a message to the location of this format, parsing for variables.
	 */
	public abstract boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> sender,
			Optional<UUID> uuid, boolean broadcast);

	/**
	 * Send a message to the location of this format, parsing for variables.
	 */
	public abstract boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender,
			Optional<UUID> uuid, boolean broadcast);

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, Map<String, Object> a, TextTemplate t, Optional<Object> sender,
			Optional<UUID> uuid, boolean broadcast) {
		Text te = get(t, a);
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender, uuid, broadcast);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, ParsableData d, TextTemplate t, Optional<Object> sender,
			Optional<UUID> uuid, boolean broadcast) {
		Text te = get(t, d);
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender, uuid, broadcast);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, Map<String, Object> a, Text te, Optional<Object> sender,
			Optional<UUID> uuid, boolean broadcast) {
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender, uuid, broadcast);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, ParsableData d, Text te, Optional<Object> sender, Optional<UUID> uuid,
			boolean broadcast) {
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender, uuid, broadcast);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, Map<String, Object> a, TextTemplate t, Optional<Object> sender) {
		Text te = get(t, a);
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, ParsableData d, TextTemplate t, Optional<Object> sender) {
		Text te = get(t, d);
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, Map<String, Object> a, Text te, Optional<Object> sender) {
		te = TextUtils.vars(te, new ParsableData().setKnown(a));
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	/**
	 * Provided send method in case all that needs to be done is send a text
	 * object to the location.
	 */
	protected boolean s(MessageReceiver target, ParsableData d, Text te, Optional<Object> sender) {
		te = TextUtils.vars(te, d);
		te = TextUtils.urls(te);
		return loc.send(te, target, this, sender);
	}

	/**
	 * Apply data to template and build.
	 */
	protected Text get(TextTemplate t, Map<String, Object> data) {
		return t.apply(data).build();
	}

	/**
	 * Apply data to template and build.
	 */
	protected Text get(TextTemplate t, ParsableData data) {
		return t.apply(parse(t, data)).build();
	}

	/**
	 * Return the map of variables parsed for the data.
	 */
	protected Map<String, Object> parse(TextTemplate t, ParsableData data) {
		if (data == null) {
			return Utils.hm();
		}
		return Ray.get().setVars(data, t, Optional.of(this));
	}

	/**
	 * @return the name of the format.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the format.
	 */
	public Format setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Create a new format from a configuration node.
	 */
	public static Format create(ConfigurationNode node) {
		if (node == null || node.isVirtual()) {
			return null;
		}
		try {
			return node.getValue(type);
		} catch (ObjectMappingException e) {
			return null;
		}
	}

	/**
	 * Set where the message is to be sent.
	 */
	public void setLocation(FormatLocation loc) {
		this.loc = loc;
	}

	/**
	 * Set where the message is to be sent.
	 */
	public void setLocation(String name) {
		this.loc = FormatLocations.fromString(name);
	}

	/**
	 * Get where the message is to be sent.
	 */
	public FormatLocation getLocation() {
		return loc;
	}

	/**
	 * @return whether this format has finished loading.
	 */
	public boolean usable() {
		return usable;
	}

	/**
	 * Get the context of the format.
	 */
	public FormatContext getContext() {
		return context;
	}

	/**
	 * Set the context of the format.
	 */
	public Format setContext(FormatContext context) {
		this.context = context;
		return this;
	}

	/**
	 * Get the config node of the format.
	 */
	public Optional<ConfigurationNode> getNode() {
		return node;
	}

	/**
	 * Set the config node of the format.
	 */
	public void setNode(Optional<ConfigurationNode> node) {
		this.node = node;
	}

	/**
	 * Return an empty Format based on the arg passed.
	 */
	public static Format valueOf(String arg) {
		switch (arg.toLowerCase().trim()) {
		case "animated":
		case "anim":
		case "animate":
		case "a":
			return af;
		case "multi":
		case "many":
		case "m":
			return mf;
		case "page":
		case "pages":
		case "paginated":
		case "p":
			return pf;
		}
		return sf;
	}

	private static AnimatedFormat af = new AnimatedFormat();
	private static StaticFormat sf = new StaticFormat();
	private static MultiFormat mf = new MultiFormat();
	private static PaginatedFormat pf = new PaginatedFormat();

	@Override
	public void applyRoot(String name, ConfigurationNode root) {
		setName(name);
		this.setNode(Utils.wrap(root));
		ConfigurationNode r = root.getNode("args");
		for (Map.Entry<String, Argument> e : args.entrySet()) {
			e.getValue().applyRoot(e.getKey(), r.getNode(e.getKey()));
		}
		applyRootInt(name, root);
	}

	public abstract void applyRootInt(String name, ConfigurationNode root);

	public Format getTopOwner() {
		Format cur = this;
		while (cur.getOwner().isPresent()) {
			cur = cur.getOwner().get();
		}
		return cur;
	}

	public Text formatVariable(String key, ParsableData data, Text var) {
		if (args == null || args.isEmpty()) {
			return var;
		}
		Argument a = args.get(key);
		if (a == null) {
			return var;
		}
		Text v2 = a.getValue(data);
		if (a.override()) {
			return v2;
		} else {
			return TextUtils.mergeOnto(v2, var);
		}
	}

	/**
	 * @return the owner
	 */
	public Optional<Format> getOwner() {
		return Utils.wrap(owner);
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(Format owner) {
		this.owner = owner;
		this.loc = owner.loc;
	}
}
