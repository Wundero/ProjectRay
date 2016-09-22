package me.Wundero.ProjectRay.config;
/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.TextUtils;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Represents a {@link TextAction} that responds to clicks. This version has
 * been modified to add support for text templates.
 *
 * @param <R>
 *            The type of the result of the action
 */
public abstract class InternalClickAction<R> extends TextAction<R> {

	/**
	 * Constructs a new {@link ClickAction} with the given result.
	 *
	 * @param result
	 *            The result of the click action
	 */
	InternalClickAction(R result) {
		super(result);
	}

	public static <T> ActionBuilder<T> builder() {
		return new ActionBuilder<T>();
	}

	public static class ActionBuilder<R> {
		@SuppressWarnings("unchecked")
		public InternalClickAction<R> build(Class<?> type) {
			switch (type.getSimpleName()) {
			case "RunTemplate":
				return (InternalClickAction<R>) TextUtils.runTemplate((TextTemplate) result);
			case "UrlTemplate":
				return (InternalClickAction<R>) TextUtils.urlTemplate((TextTemplate) result);
			case "SuggestTemplate":
				return (InternalClickAction<R>) TextUtils.suggestTemplate((TextTemplate) result);
			case "ExecuteCallback":
				return (InternalClickAction<R>) TextUtils.executeCallback((Consumer<CommandSource>) result);
			case "ChangePage":
				return (InternalClickAction<R>) TextUtils.changePage((Integer) result);
			case "RunCommand":
				return (InternalClickAction<R>) TextUtils.runCommand((String) result);
			case "OpenUrl":
				return (InternalClickAction<R>) TextUtils.openUrl((URL) result);
			default:
				return (InternalClickAction<R>) TextUtils.suggestCommand((String) result);
			}

		}

		private R result = null;

		public ActionBuilder<R> withResult(R result) {
			this.result = result;
			return this;
		}
	}

	@SuppressWarnings("rawtypes")
	public static TypeSerializer<InternalClickAction> serializer() {
		return new ts();
	}

	@SuppressWarnings("rawtypes")
	private static class ts implements TypeSerializer<InternalClickAction> {

		@Override
		public InternalClickAction deserialize(TypeToken<?> arg0, ConfigurationNode arg1)
				throws ObjectMappingException {
			final String icc = "";
			final ConfigurationNode result = arg1.getNode("result");
			switch (arg1.getNode("type").getString()) {
			case icc + "RunCommand":
				return TextUtils.runCommand(result.getString());
			case icc + "SuggestCommand":
				return TextUtils.suggestCommand(result.getString());
			case icc + "ChangePage":
				return TextUtils.changePage(result.getInt());
			case icc + "OpenUrl":
				return TextUtils.openUrl(result.getValue(TypeToken.of(URL.class)));
			case icc + "RunTemplate":
				return TextUtils.runTemplate(result.getValue(TypeToken.of(TextTemplate.class)));
			case icc + "SuggestTemplate":
				return TextUtils.suggestTemplate(result.getValue(TypeToken.of(TextTemplate.class)));
			case icc + "UrlTemplate":
				return TextUtils.urlTemplate(result.getValue(TypeToken.of(TextTemplate.class)));
			}
			return null;
		}

		@Override
		public void serialize(TypeToken<?> arg0, InternalClickAction arg1, ConfigurationNode arg2)
				throws ObjectMappingException {
			if (arg1 instanceof ATemplate) {
				arg2.getNode("result").setValue(TypeToken.of(TextTemplate.class),
						(TextTemplate) ((ATemplate) arg1).getTemplate());
			} else if (arg1 instanceof ExecuteCallback) {
				throw new ObjectMappingException("Cannot save a callback!");
			} else {
				arg2.getNode("result").setValue(arg1.getResult());
			}
			ConfigurationNode typ = arg2.getNode("type");
			typ.setValue(arg1.getClass().getSimpleName());
		}

	};

	@SuppressWarnings("unchecked")
	private ClickAction<?> toClick() {
		if (this instanceof OpenUrl) {
			return TextActions.openUrl((URL) this.getResult());
		}
		if (this instanceof RunCommand) {
			return TextActions.runCommand((String) this.getResult());
		}
		if (this instanceof SuggestCommand) {
			return TextActions.suggestCommand((String) this.getResult());
		}
		if (this instanceof ChangePage) {
			return TextActions.changePage((Integer) this.getResult());
		}
		if (this instanceof ExecuteCallback) {
			return TextActions.executeCallback((Consumer<CommandSource>) this.getResult());
		}
		TextTemplate t = ((ATemplate) this).getTemplate();
		Map<String, Object> p = Utils.sm();
		for (String k : t.getArguments().keySet()) {
			p.put(k, t.getOpenArgString() + k + t.getCloseArgString());
		}
		String plain = t.apply(p).build().toPlain();
		if (this instanceof RunTemplate) {
			return TextActions.runCommand(plain);
		}
		if (this instanceof UrlTemplate) {
			try {
				return TextActions.openUrl(new URL(plain));
			} catch (MalformedURLException e) {
			}
		}
		return TextActions.suggestCommand(plain);

	}

	@Override
	public void applyTo(Text.Builder builder) {
		builder.onClick(this.toClick());
	}

	public static final class RunTemplate extends ATemplate {
		public RunTemplate(TextTemplate template) {
			super(template);
		}
	}

	public static final class UrlTemplate extends ATemplate {
		public UrlTemplate(TextTemplate template) {
			super(template);
		}
	}

	public static final class SuggestTemplate extends ATemplate {
		public SuggestTemplate(TextTemplate template) {
			super(template);
		}
	}

	public static class ATemplate extends InternalClickAction<TextTemplate> {
		private TextTemplate template;

		public ATemplate(TextTemplate template) {
			super(template);
			this.template = template;
		}

		public TextTemplate getTemplate() {
			return template;
		}

		public void apply(Map<String, ?> args) {
			template = TextTemplate.of(template.apply(args).build());
		}

	}

	/**
	 * Opens a url.
	 */
	public static final class OpenUrl extends InternalClickAction<URL> {

		/**
		 * Constructs a new {@link OpenUrl} instance that will ask the player to
		 * open an URL when it is clicked.
		 *
		 * @param url
		 *            The url to open
		 */
		public OpenUrl(URL url) {
			super(url);
		}

	}

	/**
	 * Runs a command.
	 */
	public static final class RunCommand extends InternalClickAction<String> {

		/**
		 * Constructs a new {@link RunCommand} instance that will run a command
		 * on the client when it is clicked.
		 *
		 * @param command
		 *            The command to execute
		 */
		public RunCommand(String command) {
			super(command);
		}

	}

	/**
	 * For books, changes pages.
	 */
	public static final class ChangePage extends InternalClickAction<Integer> {

		/**
		 * Constructs a new {@link ChangePage} instance that will change the
		 * page in a book when it is clicked.
		 *
		 * @param page
		 *            The book page to switch to
		 */
		public ChangePage(int page) {
			super(Integer.valueOf(page));
		}

	}

	/**
	 * Suggests a command in the prompt.
	 */
	public static final class SuggestCommand extends InternalClickAction<String> {

		/**
		 * Constructs a new {@link SuggestCommand} instance that will suggest
		 * the player a command when it is clicked.
		 *
		 * @param command
		 *            The command to suggest
		 */
		public SuggestCommand(String command) {
			super(command);
		}

	}

	/**
	 * Execute a callback.
	 */
	public static final class ExecuteCallback extends InternalClickAction<Consumer<CommandSource>> {

		/**
		 * Constructs a new {@link ExecuteCallback} that will execute the given
		 * runnable on the server when clicked. The callback will expire after
		 * some amount of time (not particularly instantly, but not like
		 * overnight really either).
		 *
		 * @param result
		 *            The callback
		 */
		public ExecuteCallback(Consumer<CommandSource> result) {
			super(result);
		}
	}

}
