package me.Wundero.Ray.animation.effect;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.base.Function;

import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.conversation.TypePrompt;
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

public class EffectType {

	private String name;
	private Function<ConfigurationNode, Effect<?>> loader;

	private List<Setting> settings;

	public EffectType(String name, Function<ConfigurationNode, Effect<?>> loader, List<Setting> settings) {
		this.setName(name);
		this.setSettings(settings);
		this.loader = loader;
	}

	public Prompt iteratePrompts(Prompt returnTo, ConfigurationNode applyTo, boolean useOpts) {
		Prompt curPrompt = returnTo;
		for (Setting s : settings) {
			if (!useOpts && s.isOptional()) {
				continue;
			}
			curPrompt = s.getPrompt(curPrompt, applyTo);
		}
		return curPrompt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public <T> T load(ConfigurationNode s) {
		return (T) loader.apply(s);
	}

	/**
	 * @return the settings
	 */
	public List<Setting> getSettings() {
		return settings;
	}

	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public static class Setting {
		private String key;
		private Class<?> valueType;
		private boolean optional;

		public Prompt getPrompt(final Prompt returnTo, final ConfigurationNode toSet) {
			return new TypePrompt<Object>(
					TextTemplate.of(TextColors.AQUA, "What value do you want to give " + key + "?"),
					Optional.of(valueType)) {

				@Override
				public Prompt onTypeInput(Object object, String text, ConversationContext context) {
					toSet.getNode(key).setValue(object);
					return returnTo;
				}

				@Override
				public Text getQuestion(ConversationContext context) {
					return formatTemplate(context);
				}

				@Override
				public Text getFailedText(ConversationContext context, String failedInput) {
					return Text.of(TextColors.RED, "That is not a valid type");
				}
			};
		}

		public Setting(String k, Class<?> t, boolean o) {
			this.setKey(k);
			this.setValueType(t);
			this.setOptional(o);
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @param key
		 *            the key to set
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * @return the valueType
		 */
		public Class<?> getValueType() {
			return valueType;
		}

		/**
		 * @param valueType
		 *            the valueType to set
		 */
		public void setValueType(Class<?> valueType) {
			this.valueType = valueType;
		}

		/**
		 * @return the optional
		 */
		public boolean isOptional() {
			return optional;
		}

		/**
		 * @param optional
		 *            the optional to set
		 */
		public void setOptional(boolean optional) {
			this.optional = optional;
		}
	}
}
