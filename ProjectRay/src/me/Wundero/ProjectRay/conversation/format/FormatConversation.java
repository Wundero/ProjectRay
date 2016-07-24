package me.Wundero.ProjectRay.conversation.format;
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
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.config.InternalClickAction;
import me.Wundero.ProjectRay.config.InternalHoverAction;
import me.Wundero.ProjectRay.conversation.Conversation;
import me.Wundero.ProjectRay.conversation.ConversationCanceller;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.ConversationFactory;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.framework.Format;
import me.Wundero.ProjectRay.framework.FormatBuilder;
import me.Wundero.ProjectRay.framework.FormatType;
import me.Wundero.ProjectRay.framework.Group;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class FormatConversation {

	public static void start(Player player) {
		if (!player.hasPermission("ray.formatbuilder")) {
			player.sendMessage(Text.of("You do not have permission to do this!", TextColors.RED));
			return;
		}
		Conversation convo = ConversationFactory.builder(Ray.get()).withSuppression(true).withEcho(false)
				.withPrefix(Text.of("[Formats]", TextColors.AQUA)).withCanceller(ConversationCanceller.DEFAULT)
				.withFirstPrompt(new WorldPrompt()).build(player);
		convo.start();
	}

	private static class WorldPrompt extends Prompt {

		public WorldPrompt() {
			this(TextTemplate.of(Text.of("Choose a world: ", TextColors.GRAY), TextTemplate.arg("options").build()));
		}

		public WorldPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return this.formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Lists.newArrayList();
			options.add(new Option("all", Text.of("all", TextColors.GOLD, TextActions.runCommand("all"),
					TextActions.showText(Text.of("Click to choose all!", TextColors.AQUA))), "all"));
			for (World world : Sponge.getServer().getWorlds()) {
				options.add(new Option(world.getName(),
						Text.of(world.getName(), TextColors.GOLD, TextActions.runCommand(world.getName()),
								TextActions.showText(
										Text.of("Click to choose " + world.getName() + "!", TextColors.AQUA))),
						world.getName()));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(failedInput + " is not a valid world!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = Ray.get().getConfig().getNode("worlds", selected.get().getValue(), "groups");
			context.putData("node", node);
			context.putData("world", selected.get().getValue());
			return new GroupPrompt();
		}

	}

	private static class GroupPrompt extends Prompt {

		public GroupPrompt() {
			this(TextTemplate.of(Text.of("Choose a group: ", TextColors.GRAY), TextTemplate.arg("options").build()));
		}

		public GroupPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Lists.newArrayList();
			for (String g : Ray.get().getGroups().getGroups(context.getData("world").toString()).keySet()) {
				options.add(new Option(g,
						Text.of(g, TextColors.GOLD, TextActions.runCommand(g),
								TextActions.showText(Text.of("Click to select " + g + "!", TextColors.AQUA))),
						Ray.get().getGroups().getGroup(g, context.getData("world").toString())));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(failedInput + " is not a valid group!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			context.putData("node", node.getNode(selected.get().getKey(), "formats"));
			context.putData("group", selected.get().getValue());
			return new NamePrompt();
		}

	}

	private static class NamePrompt extends Prompt {

		public NamePrompt() {
			this(TextTemplate.of(Text.of("Choose a name for your format:", TextColors.GRAY)));
		}

		public NamePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			Group group = context.getData("group");
			for (Format f : group.getAllFormats()) {
				if (f.getName().equalsIgnoreCase(input.trim())) {
					return false;
				}
			}
			return true;
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
			return Text.of(failedInput + " already exists!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			node = node.getNode(text.toLowerCase().trim());
			context.putData("node", node);
			context.putData("name", text);
			return new ShouldDoTypePrompt();
		}

	}

	private static class ShouldDoTypePrompt extends Prompt {

		public ShouldDoTypePrompt() {
			this(TextTemplate.of(Text.of("Would you like to specify a type? ", TextColors.GRAY, "[✓]", TextColors.GREEN,
					TextActions.runCommand("y"), " | ", TextColors.GRAY, "[✕]", TextColors.RED,
					TextActions.runCommand("n"))));
		}

		public ShouldDoTypePrompt(TextTemplate template) {
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
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of("That is not a valid input! Try yes or no.", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (parseInput(text)) {
				return new TypePrompt();
			} else {
				context.putData("formattype", FormatType.fromString(context.getData("name")));
				return new TemplateBuilderTypePrompt();
			}
		}

	}

	private static class TypePrompt extends Prompt {

		public TypePrompt() {
			this(TextTemplate.of(Text.of("Please select a type: ", TextColors.GRAY), TextTemplate.arg("options")));
		}

		public TypePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Lists.newArrayList();
			for (FormatType type : FormatType.values()) {
				if (type == FormatType.DEFAULT) {
					continue;
				}
				options.add(new Option(type.getName(),
						Text.of(type.getName().toLowerCase().replace("_", " "), TextColors.GOLD,
								TextActions.runCommand(type.getName().toLowerCase().replace("_", " ")),
								TextActions.showText(Text.of(
										"Click here to select " + type.getName().toLowerCase().replace("_", " ") + "!",
										TextColors.AQUA))),
						type));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(failedInput + " is not a valid format type!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			context.putData("formattype", selected.get().getValue());
			FormatBuilder fb = new FormatBuilder(context.getData("node"), context.getData("name"));
			context.putData("builder", fb);
			return new TemplateBuilderTypePrompt();
		}

	}

	private static class TemplateBuilderTypePrompt extends Prompt {

		public TemplateBuilderTypePrompt() {
			this(TextTemplate.of(Text.of("Choose an element to add: ", TextColors.GRAY),
					TextTemplate.arg("options").build()));
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
			List<Option> options = Lists.newArrayList();
			options.add(
					new Option("arg",
							Text.of("argument", TextColors.GOLD, TextActions.runCommand("arg"),
									TextActions.showText(Text.of("Click this to select argument!", TextColors.AQUA))),
							"arg"));
			options.add(
					new Option("text",
							Text.of("text", TextColors.GOLD, TextActions.runCommand("text"),
									TextActions.showText(Text.of("Click this to select text!", TextColors.AQUA))),
							"text"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of("You must choose one of the options!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (text.equals("arg")) {
				return new ArgTypePrompt();
			}
			if (text.equals("text")) {
				return new TextTypePrompt();
			}
			Format format = ((FormatBuilder) context.getData("builder")).build();
			Group group = context.getData("group");
			group.addFormat(format);
			context.getHolder().sendMessage(((Conversation) context.getData("conversation")).getPrefix()
					.concat(Text.of("Format " + context.getData("name") + " successfully created!", TextColors.GREEN)));
			return null;
		}
	}

	private static class ArgTypePrompt extends Prompt {

		private ArgBuilderPrompt p = null;

		private ArgTypePrompt(ArgBuilderPrompt p) {
			this();
			this.p = p;
		}

		public ArgTypePrompt() {
			this(TextTemplate.of(Text.of("Choose an element to add: ", TextColors.GRAY),
					TextTemplate.arg("options").build()));
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
			List<Option> options = Lists.newArrayList();
			options.add(new Option("key",
					Text.of("argument", TextColors.GOLD, TextActions.runCommand("key"),
							TextActions.showText(
									Text.of("Click this to select key (creates a new argument)!", TextColors.AQUA))),
					"key"));
			options.add(
					new Option("click",
							Text.of("click", TextColors.GOLD, TextActions.runCommand("click"),
									TextActions.showText(Text.of("Click this to select click!", TextColors.AQUA))),
							"click"));
			options.add(
					new Option("hover",
							Text.of("click", TextColors.GOLD, TextActions.runCommand("hover"),
									TextActions.showText(Text.of("Click this to select hover!", TextColors.AQUA))),
							"hover"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of("You must choose one of the options!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (text.toLowerCase().trim()) {
			case "key":
				if (p != null) {
					p.apply(context);
				}
				return new ArgBuilderPrompt(null, null, null, "key");
			case "click":
			case "hover":
				if (p == null) {
					context.getHolder().sendMessage(((Conversation) context.getData("conversation")).getPrefix()
							.concat(Text.of("You must choose a key first!", TextColors.RED)));
					return this;
				}
				p.value = text.toLowerCase().trim();
				return p;
			case "done":
				if (p != null) {
					p.apply(context);
				}
				return new TemplateBuilderTypePrompt();
			}
			return null;
		}
	}

	private static class ArgBuilderPrompt extends Prompt {

		private String key;
		private InternalClickAction<?> click;
		private InternalHoverAction<?> hover;
		private String value;

		public ArgBuilderPrompt(String key, InternalClickAction<?> click, InternalHoverAction<?> hover, String value) {
			this(TextTemplate.of(Text.of("Please input a " + value + ":", TextColors.GRAY)));
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
			return formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			return Optional.empty();
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of("ERROR: This should not have triggered!", TextColors.DARK_RED);
		}

		public void apply(ConversationContext context) {
			FormatBuilder builder = context.getData("builder");
			builder = builder.withArg(key, click, hover);
			context.putData("builder", builder);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (value) {
			case "key":
				key = text;
				return new ArgTypePrompt(this);
			case "click":
				Class<?> clickType = InternalClickAction.SuggestTemplate.class;
				if (text.toLowerCase().startsWith("run:")) {
					clickType = InternalClickAction.RunTemplate.class;
					text = text.substring(4);
				}
				if (text.toLowerCase().startsWith("suggest:")) {
					text = text.substring(8);
				}
				if (text.toLowerCase().startsWith("url:")) {
					clickType = InternalClickAction.UrlTemplate.class;
					text = text.substring(4);
				}
				click = InternalClickAction.builder().withResult(Utils.parse(text, true)).build(clickType);
				return new ArgTypePrompt(this);
			case "hover":
				hover = InternalHoverAction.builder().withResult(Utils.parse(text, true))
						.build(InternalHoverAction.ShowTemplate.class);
				return new ArgTypePrompt(this);
			}
			return this;
		}

	}

	private static class TextTypePrompt extends Prompt {

		private TextBuilderPrompt p = null;

		private TextTypePrompt(TextBuilderPrompt p) {
			this();
			this.p = p;
		}

		public TextTypePrompt() {
			this(TextTemplate.of(Text.of("Choose an element to add: ", TextColors.GRAY),
					TextTemplate.arg("options").build()));
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
			List<Option> options = Lists.newArrayList();
			options.add(
					new Option("text",
							Text.of("text", TextColors.GOLD,
									TextActions.runCommand("text"), TextActions.showText(Text
											.of("Click this to select text (creates a new text)!", TextColors.AQUA))),
							"text"));
			options.add(
					new Option("click",
							Text.of("click", TextColors.GOLD, TextActions.runCommand("click"),
									TextActions.showText(Text.of("Click this to select click!", TextColors.AQUA))),
							"click"));
			options.add(
					new Option("hover",
							Text.of("click", TextColors.GOLD, TextActions.runCommand("hover"),
									TextActions.showText(Text.of("Click this to select hover!", TextColors.AQUA))),
							"hover"));
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of("You must choose one of the options!", TextColors.RED);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (text.toLowerCase().trim()) {
			case "text":
				if (p != null) {
					p.apply(context);
				}
				return new TextBuilderPrompt(null, null, null, "text");
			case "click":
			case "hover":
				if (p == null) {
					context.getHolder().sendMessage(((Conversation) context.getData("conversation")).getPrefix()
							.concat(Text.of("You must input some text first!", TextColors.RED)));
					return this;
				}
				p.value = text.toLowerCase().trim();
				return p;
			case "done":
				if (p != null) {
					p.apply(context);
				}
				return new TemplateBuilderTypePrompt();
			}
			return null;
		}
	}

	private static class TextBuilderPrompt extends Prompt {

		private Text text;
		private InternalClickAction<?> click;
		private InternalHoverAction<?> hover;
		private String value;

		public TextBuilderPrompt(Text text, InternalClickAction<?> click, InternalHoverAction<?> hover, String value) {
			this(TextTemplate.of(Text.of("Please input a " + value + ":", TextColors.GRAY)));
			this.text = text;
			this.hover = hover;
			this.click = click;
			this.value = value;
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
			return Text.of("ERROR: This should not have triggered!", TextColors.DARK_RED);
		}

		public void apply(ConversationContext context) {
			FormatBuilder builder = context.getData("builder");
			Text.Builder t = text.toBuilder();
			click.applyTo(t);
			hover.applyTo(t);
			builder = builder.withText(t.build());
			context.putData("builder", builder);
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			switch (value) {
			case "text":
				this.text = TextSerializers.FORMATTING_CODE.deserialize(text);
				return new TextTypePrompt(this);
			case "click":
				Class<?> clickType = InternalClickAction.SuggestTemplate.class;
				if (text.toLowerCase().startsWith("run:")) {
					clickType = InternalClickAction.RunTemplate.class;
					text = text.substring(4);
				}
				if (text.toLowerCase().startsWith("suggest:")) {
					text = text.substring(8);
				}
				if (text.toLowerCase().startsWith("url:")) {
					clickType = InternalClickAction.UrlTemplate.class;
					text = text.substring(4);
				}
				click = InternalClickAction.builder().withResult(Utils.parse(text, true)).build(clickType);
				return new TextTypePrompt(this);
			case "hover":
				hover = InternalHoverAction.builder().withResult(Utils.parse(text, true))
						.build(InternalHoverAction.ShowTemplate.class);
				return new TextTypePrompt(this);
			}
			return this;
		}

	}

}
