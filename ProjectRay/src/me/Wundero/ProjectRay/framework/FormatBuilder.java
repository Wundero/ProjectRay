package me.Wundero.ProjectRay.framework;
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

import java.util.Map;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.TextTemplate.Arg;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.config.DefaultArg;
import me.Wundero.ProjectRay.config.InternalClickAction;
import me.Wundero.ProjectRay.config.InternalHoverAction;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class FormatBuilder {

	private ConfigurationNode node;
	@SuppressWarnings("unused")
	private String name;
	private TextTemplate template;
	private Map<Arg, InternalClickAction<?>> clicks = Maps.newHashMap();
	private Map<Arg, InternalHoverAction<?>> hovers = Maps.newHashMap();

	public FormatBuilder(ConfigurationNode node, String name) {
		this.node = node;
		this.name = name;
		template = TextTemplate.of();
	}

	public Format build() {
		try {
			node.getNode("format").setValue(TypeToken.of(TextTemplate.class), template);
			ConfigurationNode args = node.getNode("format_args", "arguments");
			for (Arg a : clicks.keySet()) {
				args.getNode(a.getName(), "click").setValue(TypeToken.of(InternalClickAction.class), clicks.get(a));
			}
			for (Arg a : hovers.keySet()) {
				args.getNode(a.getName(), "hover").setValue(TypeToken.of(InternalHoverAction.class), hovers.get(a));
			}
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		return new Format(node);
	}

	public static FormatBuilder builder(Group group, String name) {
		return new FormatBuilder(group.getConfig().getNode("format", name), name);
	}

	public FormatBuilder withArg(String key) {
		return withArg(key, null, null);
	}

	public FormatBuilder withArg(String key, InternalHoverAction<?> hover) {
		return withArg(key, null, hover);
	}

	public FormatBuilder withArg(String key, InternalClickAction<?> click) {
		return withArg(key, click, null);
	}

	public FormatBuilder withArg(String key, InternalClickAction<?> click, InternalHoverAction<?> hover) {
		return withArg(TextTemplate.arg(key), click, hover);
	}

	public FormatBuilder withArg(DefaultArg arg) {
		return withArg(arg.getBuilder(), arg.getClick(), arg.getHover());
	}

	public FormatBuilder withArg(Arg.Builder builder, InternalHoverAction<?> hover) {
		return withArg(builder, null, hover);
	}

	public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click) {
		return withArg(builder, click, null);
	}

	public FormatBuilder withArg(Arg.Builder builder) {
		return withArg(builder, null, null);
	}

	public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click, InternalHoverAction<?> hover) {
		Arg built = builder.build();
		if (click != null) {
			clicks.put(built, click);
		}
		if (hover != null) {
			hovers.put(built, hover);
		}
		template = template.concat(TextTemplate.of(builder.build()));
		return this;
	}

	public FormatBuilder withText(Text... texts) {
		for (Text t : texts) {
			template = template.concat(TextTemplate.of(t));
		}
		return this;
	}
}
