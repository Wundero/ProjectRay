package me.Wundero.Ray.conversation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.translation.M;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;

public class TextEditConversation {

	public static void start(Player player, Text original, Consumer<Text> func) {
		if (!player.hasPermission("ray.textedit")) {
			M.sm(player, "error.nopermission");
			return;
		}
		Conversation c = ConversationFactory.builder(Ray.get().getPlugin()).withEcho(true).withSuppression(true)
				.withPrefix(Text.of(TextColors.AQUA, "[Text]"))
				.withFirstPrompt(new TextEditPrompt(TextUtils.flatten(original, true), func))
				.withCanceller(new ConversationCanceller() {

					@Override
					public boolean shouldCancel(ConversationContext context, String input) {
						return input.equalsIgnoreCase("exit");
					}

					@Override
					public void onCancel(ConversationContext context) {
						func.accept(original);
					}
				}).build(player);
		c.start();
	}

	private static TextTemplate getTemplate(List<Text> t) {
		List<Text> mod = (List<Text>) Utils.applyToAll(t, text -> {
			return text.toBuilder().onClick(TextActions.runCommand("" + t.indexOf(text))).build();
		}, () -> new ArrayList<Text>());
		Text t2 = Text.builder().append(mod).build();
		return TextTemplate.of(Text.of(TextColors.WHITE)
				.concat(Text.of(TextColors.AQUA, "Now editing text: (Click on any part to edit it, or type ",
						TextColors.GOLD, "done", TextColors.AQUA, "!)", Text.NEW_LINE)),
				(t2));
	}

	private static class TextEditPrompt extends TypePrompt<Integer> {

		private List<Text> text;
		private Consumer<Text> finished;

		public TextEditPrompt(List<Text> text, Consumer<Text> finished) {
			this(TextEditConversation.getTemplate(text));
			this.text = text;
			this.finished = finished;
		}

		private TextEditPrompt(TextTemplate template) {
			super(template, Integer.class);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext ctx, String in) {
			if (in.equalsIgnoreCase("done")) {
				return true;
			}
			if (super.isInputValid(ctx, in)) {
				int val = get(getSelected(ctx, in));
				if (val < 0 || val >= text.size()) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (text.equals("done")) {
				finished.accept(Text.join(this.text));
				return null;
			}
			return onTypeInput(get(selected), text, context);
		}

		@Override
		public Prompt onTypeInput(Integer object, String text, ConversationContext context) {
			return new EditTextPrompt(object, this.text, this.finished);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid index!");
		}
	}

	private static class EditTextPrompt extends Prompt {
		private List<Text> allText;
		private int index;
		private Consumer<Text> func;

		public EditTextPrompt(int index, List<Text> texts, Consumer<Text> finished) {
			super(TextTemplate.of(TextColors.AQUA, "Please choose what to edit, or type ", TextColors.GOLD, "done",
					TextColors.AQUA, " to finish: ", TextTemplate.arg("options").color(TextColors.GOLD).build()));
			this.index = index;
			this.func = finished;
			this.allText = texts;
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public boolean isInputValid(ConversationContext context, String input) {
			return input.equalsIgnoreCase("done") || super.isInputValid(context, input);
		}

		@Override
		public Optional<List<Option>> options(ConversationContext context) {
			List<Option> out = Utils.al();
			out.add(Option.build("text", "text"));
			out.add(Option.build("click", "click"));
			out.add(Option.build("hover", "hover"));
			return Optional.of(out);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid option!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (text.equalsIgnoreCase("done")) {
				return new TextEditPrompt(allText, func);
			} else {
				return new EditPartPrompt(text, this.allText, this.index, this.func);
			}
		}
	}

	private static class EditPartPrompt extends Prompt {
		private List<Text> all;
		private Text cur;
		private int ind;
		private Consumer<Text> func;
		private int type = 0;

		public EditPartPrompt(String type, List<Text> all, int ind, Consumer<Text> func) {
			super(TextTemplate.of(TextColors.AQUA, "Now editing ", TextColors.GOLD,
					TextActions.suggestCommand(getTypeSer(getType(type), all.get(ind))), "[" + type + "]"));
			this.all = all;
			this.ind = ind;
			this.func = func;
			this.cur = all.get(ind);
			this.type = getType(type);
		}

		private static String getTypeSer(int type, Text cur) {
			switch (type) {
			case 0:
				return TextSerializers.FORMATTING_CODE.serialize(cur);
			case 1: {
				String out = "";
				if (cur.getHoverAction().isPresent() && cur.getHoverAction().get().getResult() instanceof Text) {
					return TextSerializers.FORMATTING_CODE.serialize((Text) cur.getHoverAction().get().getResult());
				}
				return out;
			}
			case 2: {
				String out = "";
				if (cur.getClickAction().isPresent()) {
					ClickAction<?> click = cur.getClickAction().get();
					Object res = click.getResult();
					if (!(res instanceof Consumer)) {
						if (click instanceof ClickAction.ChangePage) {
							out = "page:";
						}
						if (click instanceof ClickAction.RunCommand) {
							out = "run:";
						}
						if (click instanceof ClickAction.OpenUrl) {
							out = "url:";
						}
						return out + res.toString();
					}
				}
				return out;
			}
			}
			return "";
		}

		private static ClickAction<?> get(String in) {
			String inOrg = in;
			in = in.toLowerCase();
			if (in.startsWith("page:")) {
				in = in.replaceFirst("page\\:", "");
				try {
					Integer i = Integer.parseInt(in);
					return TextActions.changePage(i);
				} catch (Exception e) {
					return TextActions.suggestCommand(inOrg.substring(5));
				}
			}
			if (in.startsWith("run:")) {
				return TextActions.runCommand(inOrg.substring(4));
			}
			if (in.startsWith("url:")) {
				Optional<URL> u = Utils.toUrlSafe(inOrg.substring(4));
				if (u.isPresent()) {
					return TextActions.openUrl(u.get());
				} else {
					return TextActions.suggestCommand(inOrg.substring(4));
				}
			}
			if (in.startsWith("suggest:")) {
				inOrg = inOrg.substring("suggest:".length());
			}
			return TextActions.suggestCommand(inOrg);
		}

		private static int getType(String type) {
			int t = 0;
			switch (type.toLowerCase().trim()) {
			case "click":
				t++;
			case "hover":
				t++;
			}
			return t;
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
			return Text.of(TextColors.DARK_RED, "Fatal error.");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			if (type == 2) {
				this.all.set(ind, cur.toBuilder().onClick(get(text)).build());
				context.sendMessage(Text.of(TextColors.AQUA, "Part now set!"));
				return new EditTextPrompt(this.ind, this.all, this.func);
			}
			Text ser = TextSerializers.FORMATTING_CODE.deserialize(text);
			if (type == 1) {
				this.all.set(ind, cur.toBuilder().onHover(TextActions.showText(ser)).build());
				context.sendMessage(Text.of(TextColors.AQUA, "Part now set!"));
				return new EditTextPrompt(this.ind, this.all, this.func);
			}
			context.sendMessage(Text.of(TextColors.AQUA, "Part now set!"));
			Text v = TextUtils.apply(ser.toBuilder(), all.get(ind).toBuilder()).build();
			this.all.set(ind, v);
			return new EditTextPrompt(this.ind, this.all, this.func);
		}
	}

}
