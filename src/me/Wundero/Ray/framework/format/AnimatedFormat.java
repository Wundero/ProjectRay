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
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import me.Wundero.Ray.animation.Animation;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.conversation.TypePrompt;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class AnimatedFormat extends Format {
	private Map<Format, Integer> frameWithDelay = Utils.sm();
	private List<Format> inOrder = Utils.sl();

	private static class Frame {
		private Format v;
		private int o;

		public Frame(Format f, int i) {
			setO(i);
			setV(f);
		}

		public Format getV() {
			return v;
		}

		public void setV(Format v) {
			this.v = v;
		}

		public int getO() {
			return o;
		}

		public void setO(int o) {
			this.o = o;
		}

	}

	public AnimatedFormat(ConfigurationNode node) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		ConfigurationNode frames = node.getNode("frames");
		Map<Format, Integer> t = Utils.sm();
		List<Frame> framez = Utils.sl();
		for (ConfigurationNode frame : frames.getChildrenMap().values()) {
			Format f = null;
			try {
				f = Format.create(frame);
				if (f == null) {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			int n = frame.getNode("number").getInt(-1);
			int d = frame.getNode("stay").getInt(10);
			if (f != null) {
				t.put(f, d);
				framez.add(new Frame(f, n));
			}
		}
		framez.sort((frame1, frame2) -> {
			return frame1.getO() - frame2.getO();
		});
		inOrder = Utils.sl(framez.stream().map(frame -> frame.getV()).collect(Collectors.toList()));
		frameWithDelay = t;
	}

	@Override
	public boolean send(MessageReceiver f, Map<String, Object> args, Optional<Object> opt) {
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (!template.send(f, args, opt)) {
				return -1;
			} else {
				if (template instanceof ExecutingFormat
						|| template.hasInternal(ExecutingFormat.class, Optional.empty())) {
					Format ft = template instanceof ExecutingFormat ? template
							: template.getInternal(ExecutingFormat.class, Optional.empty()).get();
					((ExecutingFormat) ft).execConsoles(uuid, Math.min(1000, frameWithDelay.get(template) - 1));
				}
			}
			return frameWithDelay.get(template);
		}, (template) -> {
			if (inOrder.isEmpty()) {
				return Tristate.FALSE;
			}
			if (template == null) {
				return Tristate.UNDEFINED;
			}
			return Tristate.TRUE;
		});
		anim.start();
		return true;
	}

	@Override
	public boolean send(MessageReceiver f, ParsableData data, Optional<Object> opt) {
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (!template.send(f, data, opt)) {
				return -1;
			} else {
				if (template instanceof ExecutingFormat
						|| template.hasInternal(ExecutingFormat.class, Optional.empty())) {
					Format ft = template instanceof ExecutingFormat ? template
							: template.getInternal(ExecutingFormat.class, Optional.empty()).get();
					((ExecutingFormat) ft).execConsoles(uuid, Math.min(1000, frameWithDelay.get(template) - 1));
				}
			}
			return frameWithDelay.get(template);
		}, (template) -> {
			if (inOrder.isEmpty()) {
				return Tristate.FALSE;
			}
			if (template == null) {
				return Tristate.UNDEFINED;
			}
			return Tristate.TRUE;
		});
		if (data.getObserver().isPresent()) {
			RayPlayer.get(data.getObserver().get()).queueAnimation(this.getContext(), anim);
		} else if (data.getRecipient().isPresent()) {
			RayPlayer.get(data.getRecipient().get()).queueAnimation(this.getContext(), anim);
		} else if (data.getSender().isPresent()) {
			RayPlayer.get(data.getSender().get()).queueAnimation(this.getContext(), anim);
		} else {
			anim.start();
		}
		return true;
	}

	private static class FramePrompt extends Prompt {

		private Prompt p;

		public FramePrompt(Prompt p) {
			this(TextTemplate.of(TextColors.AQUA, "Would you like to add another frame?"));
			this.p = p;
		}

		public FramePrompt(TextTemplate template) {
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
			return Text.of(TextColors.RED, "That is not a valid input! Try yes or no.");
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
			if (parseInput(text)) {
				int framenumber = context.getData("framenumber");
				ConfigurationNode frame = context.getData("frame" + framenumber);
				frame.getNode("number").setValue(framenumber);
				context.putData("framenumber", framenumber + 1);
				ConfigurationNode node = context.getData("node");
				context.putData("frame" + (framenumber + 1), node.getNode("frames", "frame" + (framenumber + 1)));
				return Format.buildConversation(new StayPrompt(p), context,
						node.getNode("frames", "frame" + (framenumber + 1)));
			} else {
				int framenumber = context.getData("framenumber");
				ConfigurationNode frame = context.getData("frame" + framenumber);
				frame.getNode("number").setValue(framenumber);
				return p;
			}
		}
	}

	private static class StayPrompt extends TypePrompt<Integer> {

		private Prompt p;

		public StayPrompt(Prompt p) {
			this(TextTemplate.of(TextColors.AQUA, "How many ticks would you like this frame to stay?"),
					Optional.of(Integer.class));
			this.p = p;
		}

		public StayPrompt(TextTemplate template, Optional<Class<? extends Integer>> type) {
			super(template, type);
		}

		@Override
		public Prompt onTypeInput(Integer object, String text, ConversationContext context) {
			ConfigurationNode n = context.getData("frame" + context.getData("framenumber"));
			n.getNode("stay").setValue(object);
			return new FramePrompt(p);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid number!");
		}

	}

	private static UUID uuid = UUID.randomUUID();

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		ConfigurationNode node = context.getData("node");
		int framenumber = 0;
		context.putData("framenumber", framenumber);
		context.putData("animated", true);
		context.putData("frame0", node.getNode("frames", "frame0"));
		context.sendMessage(Text.of("Creating first frame..."));
		return Format.buildConversation(new StayPrompt(returnTo), context, context.getData("frame0"));
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		return getInternal(clazz, index).isPresent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		if (index.isPresent() && index.get() >= 0 && index.get() < inOrder.size()) {
			Format f = this.inOrder.get(index.get());
			if (!f.getClass().equals(clazz)) {
				if (!f.getInternal(clazz, index).isPresent()) {
					return Optional.empty();
				} else {
					return f.getInternal(clazz, index);
				}
			}
			return (Optional<T>) Utils.wrap(f);
		}
		return Optional.empty();
	}
};