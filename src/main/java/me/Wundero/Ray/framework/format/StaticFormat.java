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

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.Ray.conversation.Conversation;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.framework.execute.Executable;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.location.FormatLocation;
import me.Wundero.Ray.framework.format.location.FormatLocations;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;

/**
 * Standard format type that simply sends a text to the location.
 */
public class StaticFormat extends Format {

	@Setting("executables")
	private Map<String, Executable> sendables;

	public void addExe(String name, Executable exe) {
		if (name == null || exe == null) {
			return;
		}
		this.sendables.put(name, exe);
	}

	@Override
	public boolean send(MessageReceiver f, Map<String, Object> args, Optional<Object> o, Optional<UUID> u,
			boolean broadcast) {
		if (sendables == null || sendables.isEmpty()) {
			return false;
		}
		boolean b = false;
		for (Executable e : sendables.values()) {
			Optional<? extends TextRepresentable> o2 = e.send(f);
			if (!o2.isPresent()) {
				continue;
			}
			TextRepresentable t = o2.get();
			if (t instanceof TextTemplate) {
				this.s(f, args, (TextTemplate) t, o, u, broadcast);
			} else {
				this.s(f, args, t.toText(), o, u, broadcast);
			}
		}
		return b;
	}

	@Override
	public boolean send(MessageReceiver f, ParsableData data, Optional<Object> o, Optional<UUID> u, boolean broadcast) {
		if (sendables == null || sendables.isEmpty()) {
			return false;
		}
		boolean b = false;
		for (Executable e : sendables.values()) {
			Optional<? extends TextRepresentable> o2 = e.send(f);
			if (!o2.isPresent()) {
				continue;
			}
			TextRepresentable t = o2.get();
			if (t instanceof TextTemplate) {
				this.s(f, data, (TextTemplate) t, o, u, broadcast);
			} else {
				this.s(f, data, t.toText(), o, u, broadcast);
			}
		}
		return b;
	}

	private static class LocationPrompt extends Prompt {

		private Prompt returnTo;

		public LocationPrompt(Prompt returnTo) {
			super(TextTemplate.of(TextColors.GRAY, "Please select a location: ", TextTemplate.arg("options")));
			this.returnTo = returnTo;
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> opts = Utils.al();
			for (FormatLocation f : FormatLocations.values()) {
				opts.add(Option.build(f.getName(), f.getName()));
			}
			return Optional.of(opts);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid location!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode n = context.getData("node", ConfigurationNode.class, null);
			n.getNode("location").setValue(text);
			return new TemplateBuilderTypePrompt(true, context, returnTo);
		}

	}

	private static class TemplateBuilderTypePrompt extends Prompt {

		private Prompt returnTo;

		public TemplateBuilderTypePrompt(boolean constructNewBuilder, ConversationContext context, Prompt returnTo) {
			this(returnTo);
			if (!constructNewBuilder) {
				return;
			}
			FormatBuilder fb = new FormatBuilder(context.getData("node", ConfigurationNode.class, null),
					context.getData("name", String.class, ""));
			fb.withType(context.getData("formattype", FormatContext.class, null));
			context.putData("builder", fb);
		}

		public TemplateBuilderTypePrompt(Prompt returnTo) {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Choose an element to add, or type \"done\" to finish: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
			this.returnTo = returnTo;
		}

		public TemplateBuilderTypePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			return super.isInputValid(context, input) || input.toLowerCase().trim().equals("done");
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Utils.al();
			Text t1 = Text.builder("variable").color(TextColors.GOLD).onClick(TextActions.runCommand("variable"))
					.onHover(TextActions.showText(Text.of("Click this to select variable!", TextColors.AQUA))).build();
			Text t2 = Text.builder("text").color(TextColors.GOLD).onClick(TextActions.runCommand("text"))
					.onHover(TextActions.showText(Text.of("Click this to select text!", TextColors.AQUA))).build();
			options.add(new Option("variable", t1, "variable"));
			options.add(new Option("text", t2, "text"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "You must choose one of the options, or type \"done\" to finish!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			text = text.toLowerCase();
			if (text.equals("variable")) {
				return new ArgTypePrompt(returnTo);
			}
			if (text.equals("text")) {
				return new TextTypePrompt(returnTo);
			}
			context.getData("builder", FormatBuilder.class, null).build();
			return returnTo;
		}
	}

	private static class ArgTypePrompt extends Prompt {

		private ArgBuilderPrompt p = null;
		private Prompt r;

		private ArgTypePrompt(ArgBuilderPrompt p, Prompt r) {
			this(r);
			this.p = p;
		}

		public ArgTypePrompt(Prompt r) {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Choose an element to add: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
			this.r = r;
		}

		public ArgTypePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			return super.isInputValid(context, input) || input.toLowerCase().trim().equals("done");
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Utils.al();
			options.add(new Option("key",
					Text.builder("key").color(TextColors.GOLD).onClick(TextActions.runCommand("key"))
							.onHover(TextActions.showText(
									Text.of("Click this to select key (creates a new variable)!", TextColors.AQUA)))
							.build(),
					"key"));
			Text t2 = Text.builder("click").color(TextColors.GOLD).onClick(TextActions.runCommand("click"))
					.onHover(TextActions.showText(Text.of("Click this to select click!", TextColors.AQUA))).build();
			Text t3 = Text.builder("hover").color(TextColors.GOLD).onClick(TextActions.runCommand("hover"))
					.onHover(TextActions.showText(Text.of("Click this to select hover!", TextColors.AQUA))).build();
			options.add(new Option("click", t2, "click"));
			options.add(new Option("hover", t3, "hover"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "You must choose one of the options!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (text.toLowerCase().trim()) {
			case "key":
				if (p != null) {
					p.apply(context);
				}
				return new ArgBuilderPrompt(r, null, null, null, "key");
			case "click":
				text = text
						+ " (put url:, run:, or suggest: in front of the click to change it's type (default is suggest))";
			case "hover":
				if (p == null) {
					context.getHolder().sendMessage(context.getData("conversation", Conversation.class, null)
							.getPrefix().concat(Text.of(TextColors.RED, "You must choose a key first!")));
					return this;
				}
				p.value = text.toLowerCase().trim();
				return p;
			case "done":
				if (p != null) {
					p.apply(context);
				}
				return new TemplateBuilderTypePrompt(r);
			}
			return r;
		}
	}

	private static class ArgBuilderPrompt extends Prompt {

		private String key;
		private String click;
		private String hover;
		private String value;
		private Prompt r;

		public ArgBuilderPrompt(Prompt r, String key, String click, String hover, String value) {
			this(null);
			this.r = r;
			this.key = key;
			this.hover = hover;
			this.click = click;
			this.value = value;
		}

		public ArgBuilderPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return Text.of(TextColors.GRAY, "Please input a " + value + ":");
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			return Optional.empty();
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.DARK_RED,
					"ERROR: This should not have triggered! Please report this, including the input: " + failedInput);
		}

		public void apply(ConversationContext context) {
			FormatBuilder builder = context.getData("builder", FormatBuilder.class, null);
			builder = builder.withArg(key, click, hover);
			context.putData("builder", builder);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (value.contains(" ")) {
				value = value.split(" ")[0];
			}
			switch (value) {
			case "key":
				key = text;
				return new ArgTypePrompt(this, r);
			case "click":
				click = text;
				return new ArgTypePrompt(this, r);
			case "hover":
				hover = text;
				return new ArgTypePrompt(this, r);
			}
			return this;
		}

	}

	private static class TextTypePrompt extends Prompt {

		private TextBuilderPrompt p = null;
		private Prompt r;

		private TextTypePrompt(TextBuilderPrompt p, Prompt r) {
			this(r);
			this.p = p;
		}

		public TextTypePrompt(Prompt r) {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Choose an element to add: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
			this.r = r;
		}

		public TextTypePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			return super.isInputValid(context, input) || input.toLowerCase().trim().equals("done");
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Utils.al();
			Text t1 = Text.builder("text").color(TextColors.GOLD).onClick(TextActions.runCommand("text"))
					.onHover(TextActions
							.showText(Text.of("Click this to select text (creates a new text)!", TextColors.AQUA)))
					.build();
			Text t2 = Text.builder("click").color(TextColors.GOLD).onClick(TextActions.runCommand("click"))
					.onHover(TextActions.showText(Text.of("Click this to select click!", TextColors.AQUA))).build();
			Text t3 = Text.builder("hover").color(TextColors.GOLD).onClick(TextActions.runCommand("hover"))
					.onHover(TextActions.showText(Text.of("Click this to select hover!", TextColors.AQUA))).build();
			options.add(new Option("text", t1, "text"));
			options.add(new Option("click", t2, "click"));
			options.add(new Option("hover", t3, "hover"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "You must choose one of the options!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (text.toLowerCase().trim()) {
			case "text":
				if (p != null) {
					p.apply(context);
				}
				return new TextBuilderPrompt(r, null, null, null, "text");
			case "click":
				text = text
						+ " (put url:, run:, or suggest: in front of the click to change it's type (default is suggest))";
			case "hover":
				if (p == null) {
					context.getHolder().sendMessage(context.getData("conversation", Conversation.class, null)
							.getPrefix().concat(Text.of(TextColors.RED, "You must input some text first!")));
					return this;
				}
				p.value = text.toLowerCase().trim();
				return p;
			case "done":
				if (p != null) {
					p.apply(context);
				}
				return new TemplateBuilderTypePrompt(r);
			}
			return r;
		}
	}

	private static class TextBuilderPrompt extends Prompt {

		private Text text;
		private ClickAction<?> click;
		private HoverAction<?> hover;
		private String value;
		private Prompt r;

		public TextBuilderPrompt(Prompt r, Text text, ClickAction<?> click, HoverAction<?> hover, String value) {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Please input a " + value + ":")));
			this.text = text;
			this.hover = hover;
			this.click = click;
			this.value = value;
			this.r = r;
		}

		public TextBuilderPrompt(TextTemplate template) {
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
			return Text.of(TextColors.DARK_RED,
					"ERROR: This should not have triggered! Please report this, including the input: " + failedInput);
		}

		public void apply(ConversationContext context) {
			FormatBuilder builder = context.getData("builder", FormatBuilder.class, null);
			Text.Builder t = text.toBuilder();
			if (click != null) {
				click.applyTo(t);
			}
			if (hover != null) {
				hover.applyTo(t);
			}
			builder = builder.withText(t.build());
			context.putData("builder", builder);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (value) {
			case "text":
				this.text = TextSerializers.FORMATTING_CODE.deserialize(text);
				return new TextTypePrompt(this, r);
			case "click":
				click = findClick(text);
				return new TextTypePrompt(this, r);
			case "hover":
				hover = TextActions.showText(TextUtils.convertToText(TextUtils.parse(text, true)));
				return new TextTypePrompt(this, r);
			}
			return this;
		}

	}

	private static ClickAction<?> findClick(String t) {
		String text = t.toLowerCase();
		if (text.toLowerCase().startsWith("run:")) {
			text = text.substring(4);
			return TextActions.runCommand(text);
		}
		if (text.toLowerCase().startsWith("url:")) {
			text = text.substring(4);
			Optional<URL> u = Utils.toUrlSafe(text);
			if (!u.isPresent()) {
				return TextActions.suggestCommand("ERROR");
			}
			return TextActions.openUrl(u.get());
		}
		if (text.toLowerCase().startsWith("suggest:")) {
			text = text.substring(8);
		}
		return TextActions.suggestCommand(text);
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new LocationPrompt(returnTo);
	}

	@Override
	public void applyRootInt(String name, ConfigurationNode root) {

	}

}
