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
import java.util.function.Function;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.BooleanPrompt;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class CommandFormat extends Format {

	private String command;
	private String args;
	private boolean cancel = false;
	private Format format;

	public CommandFormat(ConfigurationNode node, Format internal) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		this.format = internal;
		this.command = node.getNode("command").getString();
		if (command == null) {
			return;
		}
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		if (command.contains(" ")) {
			String[] c = command.split(" ");
			command = c[0];
			for (int i = 1; i < c.length; i++) {
				args += c[i] + " ";
			}
			args = args.trim();
		}
		this.cancel = node.getNode("cancel").getBoolean(false);
		Task.builder().intervalTicks(20).execute((task) -> {
			if (internal.usable) {
				Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
				task.cancel();
			}
		}).submit(Ray.get().getPlugin());
	}

	@Listener
	public void onCommand(SendCommandEvent event) {
		if (!event.getCause().containsType(Player.class)) {
			return;
		}
		Player s = event.getCause().first(Player.class).get();
		ParsableData d = new ParsableData();
		Map<String, Object> k = Utils.sm();
		k.put("command", event.getCommand() + " " + event.getArguments());
		d.setKnown(k);
		d.setSender(s);
		d.setRecipient(s);
		if (event.getCommand().equalsIgnoreCase(command)) {
			if (args != null && !args.isEmpty()) {
				if (event.getArguments().equalsIgnoreCase(args)) {
					if (cancel) {
						event.setCancelled(true);
					}
					send(t -> {
						s.sendMessage(t);
						return true;
					}, d);
				}
			} else {
				if (cancel) {
					event.setCancelled(true);
				}
				send(t -> {
					s.sendMessage(t);
					return true;
				}, d);
			}
		}
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new Prompt(TextTemplate.of("What command would you like to fire this format on?")) {

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
				return Text.of("Yikes! This should not happen!");
			}

			@Override
			public Prompt onInput(Optional<Option> selected, String text, ConversationContext context) {
				ConfigurationNode node = context.getData("node");
				node.getNode("command").setValue(text);
				return new BooleanPrompt(TextTemplate.of("Would you like to cancel the command?")) {

					@Override
					public Prompt onBooleanInput(boolean value, String text, ConversationContext context) {
						ConfigurationNode node = context.getData("node");
						node.getNode("cancel").setValue(value);
						return Format.buildConversation(returnTo, context, node);
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
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		return format.send(f, args);
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		return format.send(f, data);
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz) {
		if (format.getClass().equals(clazz)) {
			return true;
		}
		return format.hasInternal(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz) {
		if (hasInternal(clazz)) {
			if (format.getClass().equals(clazz)) {
				return Optional.of((T) format);
			} else {
				return format.getInternal(clazz);
			}
		}
		return Optional.empty();
	}

}
