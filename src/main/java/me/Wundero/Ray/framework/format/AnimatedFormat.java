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

import org.spongepowered.api.entity.living.player.Player;
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
import me.Wundero.Ray.utils.RayCollectors;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Represents an animated format. This type stores numerous formats, each with
 * delays.
 */
@ConfigSerializable
public class AnimatedFormat extends Format {
	@Setting("frames")
	private Map<String, Format> frameLoadable = Utils.hm();
	private Map<Format, Integer> frameWithDelay = Utils.sm();
	private List<Format> inOrder = Utils.sl();
	private Optional<Integer> initialDelay = Optional.empty();

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

	@Override
	public void applyRootInt(String n, ConfigurationNode r) {
		this.initialDelay = Utils.wrap(r.getNode("initial-delay").getInt());
		Map<Format, Integer> t = Utils.hm();
		ConfigurationNode rr = r.getNode("frames");
		List<Frame> framez = Utils.al();
		for (Map.Entry<String, Format> e : frameLoadable.entrySet()) {
			Format f = e.getValue();
			f.setOwner(this);
			ConfigurationNode rrr = rr.getNode(e.getKey());
			f.applyRoot(e.getKey(), rrr);
			int n2 = rrr.getNode("number").getInt(-1);
			int d = rrr.getNode("stay").getInt(10);
			if (f != null) {
				t.put(f, d);
				framez.add(new Frame(f, n2));
			}
		}
		framez.sort((frame1, frame2) -> {
			return frame1.getO() - frame2.getO();
		});
		inOrder = framez.stream().map(frame -> frame.getV()).collect(RayCollectors.syncList());
		initialDelay.ifPresent((delay) -> {
			Format f = new Format() {

				@Override
				public boolean equals(Object o) {
					return false;
				}

				@Override
				public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
					return returnTo;
				}

				@Override
				public boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> sender,
						Optional<UUID> u, boolean b) {
					return true;
				}

				@Override
				public boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender,
						Optional<UUID> u, boolean b) {
					return true;
				}

				@Override
				public void applyRootInt(String name, ConfigurationNode root) {
				}
			};
			inOrder.add(0, f);
			frameWithDelay.put(f, delay);
		});
		framez.forEach(f -> frameWithDelay.put(f.getV(), t.get(f.getV())));
	}

	/**
	 * Start the animation.
	 */
	@Override
	public boolean send(MessageReceiver f, Map<String, Object> args, Optional<Object> opt, Optional<UUID> u,
			boolean b) {
		if (!(f instanceof Player)) {
			return false;
		}
		final Player p = (Player) f;
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (template == null) {
				return -1;
			}
			if (!template.send(p, args, opt, u, b)) {
				return -1;
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

	/**
	 * Start the animation.
	 */
	@Override
	public boolean send(MessageReceiver f, ParsableData data, Optional<Object> opt, Optional<UUID> u, boolean b) {
		if (!(f instanceof Player)) {
			return false;
		}
		final Player p = (Player) f;
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (template == null) {
				return -1;
			}
			if (!template.send(p, data, opt, u, b)) {
				return -1;
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
			RayPlayer.get(data.getObserver().get()).queueAnimation(this.getLocation(), anim);
		} else if (data.getRecipient().isPresent()) {
			RayPlayer.get(data.getRecipient().get()).queueAnimation(this.getLocation(), anim);
		} else if (data.getSender().isPresent()) {
			RayPlayer.get(data.getSender().get()).queueAnimation(this.getLocation(), anim);
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
				int framenumber = context.getData("framenumber", int.class, 0);
				ConfigurationNode frame = context.getData("frame" + framenumber, ConfigurationNode.class, null);
				frame.getNode("number").setValue(framenumber);
				context.putData("framenumber", framenumber + 1);
				ConfigurationNode node = context.getData("node", ConfigurationNode.class, null);
				context.putData("frame" + (framenumber + 1), node.getNode("frames", "frame" + (framenumber + 1)));
				return Format.buildConversation(new StayPrompt(p), context,
						node.getNode("frames", "frame" + (framenumber + 1)));
			} else {
				int framenumber = context.getData("framenumber", int.class, 0);
				ConfigurationNode frame = context.getData("frame" + framenumber, ConfigurationNode.class, null);
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
			int num = context.getData("framenumber", int.class, 0);
			ConfigurationNode n = context.getData("frame" + num, ConfigurationNode.class, null);
			n.getNode("stay").setValue(text);
			return new FramePrompt(p);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			System.out.println("Important prompt " + this.getClass().getName());
			return formatTemplate(context);
		}

		@Override
		public Text getFailedText(ConversationContext context, String failedInput) {
			return Text.of(TextColors.RED, "That is not a valid number!");
		}

	}

	/**
	 * Creation prompt.
	 */
	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		ConfigurationNode node = context.getData("node", ConfigurationNode.class, null);
		int framenumber = 0;
		context.putData("framenumber", framenumber);
		context.putData("animated", true);
		context.putData("frame0", node.getNode("frames", "frame0"));
		context.sendMessage(Text.of("Creating first frame..."));
		return Format.buildConversation(new StayPrompt(returnTo), context,
				context.getData("frame0", ConfigurationNode.class, null));
	}

}