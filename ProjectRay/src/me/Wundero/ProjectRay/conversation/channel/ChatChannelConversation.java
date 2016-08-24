package me.Wundero.ProjectRay.conversation.channel;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.Conversation;
import me.Wundero.ProjectRay.conversation.ConversationCanceller;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Cancel;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Chat;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Finish;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Next;
import me.Wundero.ProjectRay.conversation.ConversationEvent.Start;
import me.Wundero.ProjectRay.conversation.ConversationFactory;
import me.Wundero.ProjectRay.conversation.ConversationListener;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.conversation.TypePrompt;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

public class ChatChannelConversation {

	// builds chat channels

	public static void start(Player player) {
		if (!player.hasPermission("ray.channelbuilder")) {
			player.sendMessage(Text.of(TextColors.RED, "You do not have permission to do this!"));
			return;
		}
		if (Ray.get().getChannels().getNode() == null) {
			player.sendMessage(Text.of(TextColors.RED, "Channels are not setup!"));
			return;
		}
		player.sendMessage(Text.of(TextColors.AQUA, "[Channels] ", TextColors.GREEN,
				"You can cancel at any time by typing \"exit\"."));
		Conversation convo = ConversationFactory.builder(Ray.get()).withSuppression(true).withEcho(true)
				.withPrefix(Text.of(TextColors.AQUA, "[Channels]")).withCanceller(new ConversationCanceller() {

					@Override
					public boolean shouldCancel(ConversationContext context, String input) {
						return input.toLowerCase().trim().equals("exit");
					}

					@Override
					public void onCancel(ConversationContext context) {
						if (!context.hasData("node")) {
							return;
						}
						ConfigurationNode node = context.getData("node");
						if (node != null) {
							node.setValue(null);
						}
					}
				}).withListener(new ConversationListener() {
					@Override
					public void onChat(Chat chat) {
					}

					@Override
					public void onFinish(Finish finish) {
						try {
							Ray.get().getChannels().addChannel((ConfigurationNode) finish.getContext().getData("node"));
						} catch (ObjectMappingException e) {
							Utils.printError(e);
						}
					}

					@Override
					public void onCancel(Cancel cancel) {
						cancel.getContext().getHolder().sendMessage(
								Text.of(TextColors.AQUA, "[Channels] ", TextColors.RED, "Channel creation cancelled!"));
					}

					@Override
					public void onNext(Next next) {
					}

					@Override
					public void onStart(Start start) {
					}
				}).withFirstPrompt(new NamePrompt()).build(player);
		convo.start();
	}

	private static class NamePrompt extends Prompt {

		public NamePrompt() {
			this(TextTemplate.of(Text.of(TextColors.AQUA, "Please choose a name for the channel:")));
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
			return Text.of(TextColors.RED + "Uh oh");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			context.putData("name", text);
			ConfigurationNode node = Ray.get().getChannels().getNode().getNode(text);
			node.getNode("name").setValue(text);
			context.putData("node", node);
			return new OptionPrompt();
		}
	}

	private static class OptionPrompt extends Prompt {

		public OptionPrompt() {
			this(TextTemplate.of(
					Text.of(TextColors.AQUA, "Choose what would you like to set, or type \"done\" to finish: "),
					TextTemplate.arg("options").color(TextColors.GOLD).build()));
		}

		public OptionPrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			if (input.equalsIgnoreCase("done")) {
				return true;
			}
			return super.isInputValid(context, input);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> out = Utils.sl();
			out.add(new Option("permission",
					Text.builder("permission").onClick(TextActions.runCommand("permission"))
							.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select permission!")))
							.build(),
					"string"));
			out.add(new Option("tag", Text.builder("tag").onClick(TextActions.runCommand("tag"))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select tag!"))).build(),
					"string"));
			out.add(new Option("range", Text.builder("range").onClick(TextActions.runCommand("range"))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select range!"))).build(),
					"double"));
			out.add(new Option("hidden", Text.builder("hidden").onClick(TextActions.runCommand("hidden"))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select hidden!"))).build(),
					"boolean"));
			out.add(new Option("autojoin", Text.builder("autojoin").onClick(TextActions.runCommand("autojoin"))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select autojoin!"))).build(),
					"boolean"));
			out.add(new Option("obfuscate", Text.builder("obfuscate").onClick(TextActions.runCommand("obfuscate"))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to select obfuscate!"))).build(),
					"boolean"));
			return Optional.of(out);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid type!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (!selected.isPresent()) {
				return null;
			}
			switch ((String) selected.get().getValue()) {
			case "string":
				return new StringPrompt(selected.get().getKey());
			case "double":
				return new DoublePrompt(selected.get().getKey());
			case "boolean":
				return new BooleanPrompt(selected.get().getKey());
			}
			return null;
		}

	}

	private static class DoublePrompt extends TypePrompt<Double> {

		private String node;

		public DoublePrompt(String node) {
			this(TextTemplate.of(Text.of(TextColors.AQUA, "Set the value of " + node + ":")));
			this.node = node;
		}

		public DoublePrompt(TextTemplate template) {
			super(template, Optional.of(Double.class));
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid number!");
		}

		@Override
		public Prompt onTypeInput(Double object, String text, ConversationContext context) {
			ConfigurationNode n = context.getData("node");
			n.getNode(node).setValue(object);
			return new OptionPrompt();
		}

	}

	private static class BooleanPrompt extends Prompt {

		private String node;

		public BooleanPrompt(String node) {
			this(TextTemplate.of(Text.of(TextColors.AQUA, "Set the value of " + node + ":")));
			this.node = node;
		}

		public BooleanPrompt(TextTemplate template) {
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
			return Text.of(TextColors.RED, "That is not a valid boolean! Try yes or no.");
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
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			boolean b = parseInput(text);
			ConfigurationNode n = context.getData("node");
			n.getNode(node).setValue(b);
			return new OptionPrompt();
		}

	}

	private static class StringPrompt extends Prompt {

		private String node;

		public StringPrompt(String node) {
			this(TextTemplate.of(Text.of(TextColors.AQUA, "Set the value of " + node + ":")));
			this.node = node;
		}

		public StringPrompt(TextTemplate template) {
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
			return Text.of(TextColors.RED, "uh oh");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode n = context.getData("node");
			n.getNode(node).setValue(text);
			return new OptionPrompt();
		}

	}
}
