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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.conversation.BooleanPrompt;
import me.Wundero.Ray.conversation.ConversationContext;
import me.Wundero.Ray.conversation.Option;
import me.Wundero.Ray.conversation.Prompt;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * This format type represents a type which will be called when a command is
 * fired by a player.
 * 
 * TODO move variables in onCommand
 */
public class CommandFormat extends Format {

	private String command;
	private String args;
	private boolean cancel = false;
	private Format format;

	/**
	 * Create a command format.
	 */
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

	// Notes:
	// If a CommandContext goes through some event that is a pre in the future,
	// use that instead.
	// CommandContext parse for named put as vars.
	/**
	 * Parse a command. Currently there is no support for recipients other than
	 * the player who sent the command.
	 */
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
					send(s, d, Optional.of(s));
				}
			} else {
				if (cancel) {
					event.setCancelled(true);
				}
				send(s, d, Optional.of(s));
			}
		}
	}

	/**
	 * Creation prompt
	 */
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

	/**
	 * Send the message to the internal format.
	 */
	@Override
	public boolean send(MessageReceiver target, Map<String, Object> args, Optional<Object> opt) {
		return format.send(target, args, opt);
	}

	/**
	 * Send the message to the internal format.
	 */
	@Override
	public boolean send(MessageReceiver target, ParsableData data, Optional<Object> opt) {
		return format.send(target, data, opt);
	}

	/**
	 * Whether the wrapped format is or has the right class.
	 */
	@Override
	public boolean hasInternal(Class<? extends Format> clazz, Optional<Integer> index) {
		if (format.getClass().equals(clazz)) {
			return true;
		}
		return format.hasInternal(clazz, index);
	}

	/**
	 * Returned the wrapped format if it is the right class. If it is not,
	 * return what the wrapped format's getInternal returns.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz, Optional<Integer> index) {
		if (hasInternal(clazz, index)) {
			if (format.getClass().equals(clazz)) {
				return Optional.of((T) format);
			} else {
				return format.getInternal(clazz, index);
			}
		}
		return Optional.empty();
	}

}
