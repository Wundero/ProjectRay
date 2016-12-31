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

import me.Wundero.Ray.animation.effect.Effect;
import me.Wundero.Ray.animation.effect.EffectType;
import me.Wundero.Ray.animation.effect.EffectTypes;
import me.Wundero.Ray.conversation.BooleanPrompt;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Format type representing animated "effects". Like AnimatedFormat, however
 * only storing an effect loaded from config.
 */
public class EffectFormat extends Format {

	private Effect<?> effect;
	private String type;
	private EffectType etype;

	/**
	 * Create a new effect format.
	 */
	public EffectFormat(ConfigurationNode node) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		this.type = node.getNode("type").getString();
		if (type == null) {
			throw new NullPointerException("Type cannot be null!");
		}
		this.etype = EffectTypes.from(type);
		this.effect = etype.load(node);
	}

	/**
	 * Creation prompt.
	 */
	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new Prompt(TextTemplate.of(TextColors.AQUA, "What type of effect would you like to use? ",
				TextTemplate.arg("options"))) {

			@Override
			public Text getQuestion(ConversationContext context) {
				return formatTemplate(context);
			}

			@Override
			public Optional<List<Option>> options(ConversationContext context) {
				List<Option> opts = Utils.al();
				for (EffectType et : EffectTypes.values()) {
					opts.add(Option.build(et.getName(), et.getName()));
				}
				return Optional.of(opts);
			}

			@Override
			public Text getFailedText(ConversationContext context, String failedInput) {
				return Text.of(TextColors.RED, "That is not a valid option!");
			}

			@Override
			public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
				ConfigurationNode node = context.getData("node", ConfigurationNode.class, null);
				node.getNode("type").setValue(text);
				return new BooleanPrompt(TextTemplate.of(TextColors.AQUA,
						"Would you like to include optional settings? ", TextTemplate.arg("options")), true) {

					@Override
					public Prompt onBooleanInput(boolean value, String text, ConversationContext context) {
						return EffectTypes.from(text).iteratePrompts(returnTo, node, value);
					}

					@Override
					public Text getQuestion(ConversationContext context) {
						return formatTemplate(context);
					}

				};
			}

		};
	}

	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		return Optional.empty();
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		return false;
	}

	/**
	 * Play the effect.
	 */
	@Override
	public boolean send(MessageReceiver target, Map<String, Object> data, Optional<Object> sender, Optional<UUID> u,
			boolean broadcast) {
		if (!(target instanceof Player)) {
			return false;
		}
		effect.setupAnimation((Player) target, data, (text, player) -> {
			return s(target, data, text, sender, u, broadcast);
		});
		effect.start();
		return true;
	}

	/**
	 * Play the effect.
	 */
	@Override
	public boolean send(MessageReceiver target, ParsableData data, Optional<Object> sender, Optional<UUID> u,
			boolean broadcast) {
		if (!(target instanceof Player)) {
			return false;
		}
		effect.setupAnimation((Player) target, data, (text, player) -> {
			return s(target, data, text, sender, u, broadcast);
		});
		effect.start();
		return true;
	}

}
