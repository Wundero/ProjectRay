package me.Wundero.Ray.conversation.format;
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
import org.spongepowered.api.world.World;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.conversation.Conversation;
import me.Wundero.Ray.conversation.ConversationCanceller;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.ConversationEvent.Cancel;
import me.Wundero.Ray.conversation.ConversationEvent.Chat;
import me.Wundero.Ray.conversation.ConversationEvent.Finish;
import me.Wundero.Ray.conversation.ConversationEvent.Next;
import me.Wundero.Ray.conversation.ConversationEvent.Start;
import me.Wundero.Ray.conversation.ConversationFactory;
import me.Wundero.Ray.conversation.ConversationListener;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.framework.Group;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.context.FormatContexts;
import me.Wundero.Ray.translation.I18n;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * A class that creates a format from a conversation. This method is far better
 * to use than to directly edit the configuration file, as formats use
 * serialized text template objects, which are incredibly complicated in config.
 */
public class FormatConversation {

	/**
	 * Start the format creation conversation.
	 */
	public static void start(Player player) {
		if (!player.hasPermission("ray.formatbuilder")) {
			player.sendMessage(Text.of(TextColors.RED, "You do not have permission to do this!"));
			return;
		}
		player.sendMessage(Text.of(TextColors.AQUA, "[Formats] ").concat(I18n.t("conversation.exit.reminder")));
		Conversation convo = ConversationFactory.builder(Ray.get()).withSuppression(true).withEcho(true)
				.withPrefix(Text.of(TextColors.AQUA, "[Formats]")).withCanceller(new ConversationCanceller() {

					@Override
					public boolean shouldCancel(ConversationContext context, String input) {
						return input.toLowerCase().trim().equals("exit");
					}

					@Override
					public void onCancel(ConversationContext context) {
						if (!context.hasData("wipable node")) {
							return;
						}
						ConfigurationNode node = context.getData("wipable node");
						boolean wipegroup = context.getData("wipegroup", false);
						if (node != null) {
							if (wipegroup) {
								node.getParent().setValue(null);
							} else {
								node.setValue(null);
							}
						}
						context.getHolder().sendMessage(
								Text.of(TextColors.AQUA, "[Formats] ", TextColors.RED, "Format creation cancelled!"));
					}

				}).withListener(new ConversationListener() {

					@Override
					public void onChat(Chat chat) {
					}

					@Override
					public void onFinish(Finish finish) {
						ConversationContext context = finish.getContext();
						context.sendMessage(Text.of(TextColors.GREEN,
								"Format created successfully! Please restart for changes to take effect."));
						Ray.get().getPlugin().save();
					}

					@Override
					public void onCancel(Cancel cancel) {
					}

					@Override
					public void onNext(Next next) {
					}

					@Override
					public void onStart(Start start) {
					}
				}).withFirstPrompt(new WorldPrompt()).build(player);
		convo.start();
	}

	private static class WorldPrompt extends Prompt {

		public WorldPrompt() {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Choose a world: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
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
			List<Option> options = Utils.al();
			options.add(new Option("all",
					Text.builder("all").color(TextColors.GOLD).onClick(TextActions.runCommand("all"))
							.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to choose all!"))).build(),
					"all"));
			for (World world : Sponge.getServer().getWorlds()) {
				options.add(new Option(world.getName(),
						Text.builder(world.getName()).color(TextColors.GOLD)
								.onClick(TextActions.runCommand(world.getName()))
								.onHover(TextActions
										.showText(Text.of(TextColors.AQUA, "Click to choose " + world.getName() + "!")))
								.build(),
						world.getName()));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, failedInput + " is not a valid world!");
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
			this(TextTemplate.of(
					Text.of(TextColors.GRAY, "Choose a group, or type in a different name to create one: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
		}

		public GroupPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			return true;
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> options = Utils.al();
			for (String g : Ray.get().getGroups().getGroups(context.getData("world").toString()).keySet()) {
				options.add(new Option(g,
						Text.builder(g).color(TextColors.GOLD).onClick(TextActions.runCommand(g))
								.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to select " + g + "!")))
								.build(),
						Ray.get().getGroups().getGroup(g, context.getData("world").toString())));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of();
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			context.putData("node", node.getNode(selected.isPresent() ? selected.get().getKey() : text, "formats"));
			if (selected.isPresent()) {
				context.putData("group", selected.get().getValue());
			} else {
				Group g = Ray.get().getGroups().load(node.getNode(text));
				context.putData("group", g);
				context.putData("wipegroup", true);
			}
			return new NamePrompt();
		}

	}

	private static class NamePrompt extends Prompt {

		public NamePrompt() {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Choose a name for your format:")));
		}

		public NamePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			Group group = context.getData("group");
			for (Format f : group.getAllFormats().get()) {
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
			return Text.of(TextColors.RED, failedInput + " already exists!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			node = node.getNode(text.toLowerCase().trim());
			context.putData("node", node);
			context.putData("wipable node", node.getParent().getNode(node.getKey()));
			context.putData("name", text);
			return new ShouldDoTypePrompt();
		}

	}

	private static class ShouldDoTypePrompt extends Prompt {

		public ShouldDoTypePrompt() {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Would you like to specify a context? "),
					Text.builder("[" + '\u2713' + "]").color(TextColors.GREEN).onClick(TextActions.runCommand("y"))
							.build(),
					Text.of(TextColors.GRAY, " | "), Text.builder("[" + '\u2715' + "]").color(TextColors.RED)
							.onClick(TextActions.runCommand("n")).build()));
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
			return Text.of(TextColors.RED, "That is not a valid input! Try yes or no.");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (parseInput(text)) {
				return new TypePrompt();
			} else {
				context.putData("formattype", FormatContext.fromString(context.getData("name")));
				ConfigurationNode node = context.getData("node");
				String type = ((FormatContext) context.getData("formattype")).getName();
				node.getNode("context").setValue(type);
				return Format.buildConversation(null, context, node);
			}
		}

	}

	private static class TypePrompt extends Prompt {

		public TypePrompt() {
			this(TextTemplate.of(Text.of(TextColors.GRAY, "Please select a context: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
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
			List<Option> options = Utils.al();
			for (FormatContext type : FormatContexts.values()) {
				if (type == FormatContexts.DEFAULT) {
					continue;
				}
				options.add(new Option(
						type.getName().toLowerCase().replace("_",
								" "),
						Text.builder(type.getName().toLowerCase().replace("_", " ")).color(TextColors.GOLD)
								.onClick(TextActions.runCommand(type.getName().toLowerCase().replace("_", " ")))
								.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select "
										+ type.getName().toLowerCase().replace("_", " ") + "!")))
								.build(),
						type));
			}
			return Optional.of(options);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, failedInput + " is not a valid format type!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			context.putData("formattype", selected.get().getValue());
			ConfigurationNode node = context.getData("node");
			String type = ((FormatContext) context.getData("formattype")).getName();
			node.getNode("context").setValue(type);
			return Format.buildConversation(null, context, node);
		}

	}

}
