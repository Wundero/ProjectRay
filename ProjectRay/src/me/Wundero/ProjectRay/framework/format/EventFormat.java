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
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Option;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class EventFormat extends Format {

	private Optional<String> eventClass = Optional.empty();

	private Format internal;

	public EventFormat(ConfigurationNode node, Format f) {
		super(node);
		if (node == null || node.isVirtual()) {
			return;
		}
		internal = f;
		String cname = node.getNode("event").getString();
		if (cname != null) {
			eventClass = Optional.of(cname);
		}
		Task.builder().intervalTicks(20).execute((task) -> {
			if (f.usable) {
				Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
				task.cancel();
			}
		}).submit(Ray.get().getPlugin());
	}

	private boolean checkClass(Class<?> clazz) {
		if (!eventClass.isPresent()) {
			return false;
		}
		return Utils.classinstanceof(eventClass.get(), clazz);
	}

	@Listener
	public void onEvent(Event e) {
		if (checkClass(e.getClass())) {
			ParsableData data = new ParsableData();
			int i = 0;
			for (Object o : e.getCause().all()) {
				if (o instanceof Player) {
					if (i == 0) {
						data.setSender((Player) o);
					} else if (i == 1) {
						data.setRecipient((Player) o);
					} else if (i == 2) {
						data.setObserver((Player) o);
					}
					i++;
				}
			}
			Map<String, Object> map = Utils.sm();
			for (Map.Entry<String, Object> key : e.getCause().getNamedCauses().entrySet()) {
				if (!(key.getValue() instanceof Player)) {
					map.put(key.getKey(), key.getValue());
				}
			}
			data.setKnown(map);
			send((text) -> {
				Sponge.getServer().getBroadcastChannel().send(text);
				return true;
			}, data);
		}
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		return internal.send(f, args);
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		return internal.send(f, data);
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
		return new Prompt(TextTemplate.of("What event would you like to fire this format on?")) {

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
				node.getNode("event").setValue(text);
				return Format.buildConversation(returnTo, context, node);
			}

		};
	}

}
