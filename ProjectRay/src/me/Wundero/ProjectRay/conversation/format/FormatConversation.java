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
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.Conversation;
import me.Wundero.ProjectRay.conversation.ConversationCanceller;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.ConversationFactory;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.framework.Format;
import me.Wundero.ProjectRay.framework.Group;
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
			this(TextTemplate.of("Would you like to specify a type?", TextColors.GRAY));
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

			}
		}

	}

	private static class TypePrompt extends Prompt {

		public TypePrompt(TextTemplate template) {
			super(template);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	// TODO template prompt
}
