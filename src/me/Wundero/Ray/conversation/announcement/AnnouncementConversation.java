package me.Wundero.Ray.conversation.announcement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.conversation.Conversation;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.ConversationEvent.Cancel;
import me.Wundero.Ray.conversation.ConversationEvent.Chat;
import me.Wundero.Ray.conversation.ConversationEvent.Finish;
import me.Wundero.Ray.conversation.ConversationEvent.Next;
import me.Wundero.Ray.conversation.ConversationEvent.Start;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.conversation.ConversationFactory;
import me.Wundero.Ray.conversation.ConversationListener;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.conversation.TypePrompt;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

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

public class AnnouncementConversation {

	public static void start(Player player) {
		if (!player.hasPermission("ray.announcementbuilder")) {
			player.sendMessage(Text.of(TextColors.RED, "You do not have permission to do this!"));
			return;
		}
		Map<String, Object> data = Utils.sm();
		ConfigurationNode node = Ray.get().getConfig().getNode("worlds", "all", "groups", "default");
		data.put("node", node);
		Conversation c = ConversationFactory.builder(Ray.get().getPlugin()).withEcho(true)
				.withFirstPrompt(new NamePrompt()).withInitialContext(data).withSuppression(true)
				.withPrefix(Text.of(TextColors.BLACK, "[", TextColors.AQUA, "Announcement", TextColors.BLACK, "]"))
				.withListener(new ConversationListener() {

					@Override
					public void onChat(Chat chat) {
					}

					@Override
					public void onFinish(Finish finish) {
						finish.getContext().sendMessage(Text.of(TextColors.GREEN, "Announcement created!"));
					}

					@Override
					public void onCancel(Cancel cancel) {
						cancel.getContext().sendMessage(Text.of(TextColors.RED, "Announcement creation cancelled!"));
						if (cancel.getContext().hasData("wipable node")) {
							ConfigurationNode wn = cancel.getContext().getData("wipable node");
							wn.setValue(null);
						}
					}

					@Override
					public void onNext(Next next) {
					}

					@Override
					public void onStart(Start start) {
						start.getContext().sendMessage(Text.of(TextColors.AQUA, "Now creating an announcement!"));
						start.getContext().sendMessage(
								Text.of(TextColors.AQUA, "You can cancel at any time by typing \"exit\"!"));
						start.getContext().sendMessage(Text.of(TextColors.AQUA,
								"The announcement will be put under world all in group default, but you can move it to wherever you like."));
					}
				}).build(player);
		c.start();
	}

	private static class NamePrompt extends Prompt {

		public NamePrompt() {
			this(TextTemplate.of(TextColors.AQUA, "What would you like to call the announcement?"));
		}

		public NamePrompt(TextTemplate template) {
			super(template);
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
			return Text.of(TextColors.RED, "Something went wrong! Please report this.");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("node");
			context.putData("node", node.getNode(text, "frames", text));
			context.putData("wipable node", node.getNode(text));
			return new DelayPrompt();
		}

	}

	private static class DelayPrompt extends TypePrompt<Integer> {

		public DelayPrompt() {
			this(TextTemplate.of(TextColors.AQUA,
					"Please select the delay (default time unit is seconds, type #[type] to change it [i.e. 25m]):"),
					Optional.of(Integer.class));
		}

		public DelayPrompt(TextTemplate template, Optional<Class<? extends Integer>> type) {
			super(template, type);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			try {
				Integer.valueOf(input);
			} catch (Exception e) {
				Pattern p = Pattern.compile("[0-9]+[smhd]", Pattern.CASE_INSENSITIVE);
				if (p.matcher(input).matches()) {
					char type = input.toLowerCase().charAt(input.length() - 1);
					String inputx = input.substring(0, input.length() - 1);
					Integer i = Integer.valueOf(inputx);
					switch (type) {
					case 'd':
						inputx = "" + TimeUnit.DAYS.toSeconds(i) * 20;
						break;
					case 'h':
						inputx = "" + TimeUnit.HOURS.toSeconds(i) * 20;
						break;
					case 'm':
						inputx = "" + TimeUnit.MINUTES.toSeconds(i) * 20;
						break;
					}
					return super.isInputValid(context, inputx);
				} else {
					return false;
				}
			}
			return super.isInputValid(context, input);
		}

		@Override
		public Prompt onTypeInput(Integer object, String text, ConversationContext context) {
			if (object == null) {
				context.sendMessage(getFailedText(context, text));
				return this;
			}
			ConfigurationNode node = context.getData("node");
			node.getNode("stay").setValue(object);
			String k = node.getKey().toString();
			return Format.buildConversation(null, context, node.getNode("frames", k), Format.valueOf("multi"));
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid number format!");
		}

	}

}
