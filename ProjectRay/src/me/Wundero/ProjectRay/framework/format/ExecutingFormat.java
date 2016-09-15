package me.Wundero.ProjectRay.framework.format;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.BooleanPrompt;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class ExecutingFormat extends Format {

	private Format wrapped;
	private Map<String, Boolean> cmds = Utils.sm();

	public ExecutingFormat(ConfigurationNode node, Format internal) {
		super(node);
		if(node==null) {
			return;
		}
		this.wrapped = internal;
		ConfigurationNode cmdnode = node.getNode("commands");
		for (ConfigurationNode cmd : cmdnode.getChildrenMap().values()) {
			String c = cmd.getNode("command").getString();
			boolean console = cmd.getNode("use-console").getBoolean(true);
			if (c != null) {
				cmds.put(c, console);
			}
		}
	}

	public boolean hasInternal(Class<? extends Format> clazz) {
		if (wrapped.getClass().equals(clazz)) {
			return true;
		}
		return wrapped.hasInternal(clazz);
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new CommandPrompt(returnTo);
	}

	private static class CommandPrompt extends Prompt {

		public CommandPrompt(Prompt r) {
			this(TextTemplate.of(TextColors.AQUA, "What command would you like this format to execute?"), r);
		}

		private Prompt r;

		public CommandPrompt(TextTemplate template, Prompt r) {
			super(template);
			this.r = r;
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
			return Text.of(TextColors.RED, "This should not happen!");
		}

		@Override
		public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
			ConfigurationNode supnode = context.getData("node");
			ConfigurationNode cmdnode = supnode.getNode("commands", text);
			cmdnode.getNode("command").setValue(text);
			context.putData("commandnode", cmdnode);
			return new ConsolePrompt(r);
		}

	}

	private static class ConsolePrompt extends BooleanPrompt {

		private Prompt r;

		public ConsolePrompt(Prompt r) {
			super(TextTemplate.of(TextColors.AQUA,
					"Should this command be executed by the console? (If not, it will be executed by all receiving players)"));
			this.r = r;
		}

		@Override
		public Prompt onBooleanInput(boolean value, String text, ConversationContext context) {
			ConfigurationNode node = context.getData("commandnode");
			node.getNode("use-console").setValue(value);
			return new AnotherCommandPrompt(r);
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

	}

	private static class AnotherCommandPrompt extends BooleanPrompt {
		private Prompt r;

		public AnotherCommandPrompt(Prompt r) {
			super(TextTemplate.of(TextColors.AQUA, "Would you like to add another command?"));
			this.r = r;
		}

		@Override
		public Prompt onBooleanInput(boolean value, String text, ConversationContext context) {
			if (value) {
				return new CommandPrompt(r);
			} else {
				return r;
			}
		}

		@Override
		public Text getQuestion(ConversationContext context) {
			return formatTemplate(context);
		}

	}

	private List<UUID> senders = Utils.sl();

	public boolean execConsoles(UUID from, long timeoutMillis) {
		if (senders.contains(from)) {
			return false;
		}
		senders.add(from);
		for (Map.Entry<String, Boolean> entry : cmds.entrySet()) {
			if (entry.getValue()) {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(), entry.getKey());
			}
		}
		Task.builder().delay(timeoutMillis, TimeUnit.MILLISECONDS).execute(() -> {
			senders.remove(from);
		}).submit(Ray.get().getPlugin());
		return true;
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		return wrapped.send(f, args);
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		Function<Text, Boolean> f2 = (text) -> {
			if (data.getRecipient().isPresent()) {
				for (Map.Entry<String, Boolean> entry : cmds.entrySet()) {
					if (!entry.getValue()) {
						Sponge.getCommandManager().process(data.getRecipient().get(), entry.getKey());
					}
				}
			}
			return f.apply(text);
		};
		return wrapped.send(f2, data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz) {
		if (!hasInternal(clazz)) {
			return Optional.empty();
		}
		if (wrapped.getClass().equals(clazz)) {
			return Optional.of((T) wrapped);
		}
		return wrapped.getInternal(clazz);
	}

}