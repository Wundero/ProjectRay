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

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.TextTemplate.Arg;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.framework.Group;
import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.context.FormatContexts;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * Builder class for formats - saves to node then loads node.
 */
public class FormatBuilder {

	private ConfigurationNode node;
	@SuppressWarnings("unused")
	private String name;
	private TextTemplate template;
	private Map<Arg, String> clicks = Utils.hm();
	private Map<Arg, String> hovers = Utils.hm();
	private Optional<FormatContext> type;

	/**
	 * Create a new format builder
	 */
	public FormatBuilder(ConfigurationNode node, String name) {
		this.node = node;
		this.name = name;
		template = TextTemplate.of();
	}

	/**
	 * Build the format.
	 */
	public Format build() {
		try {
			node.getNode("format").setValue(TypeToken.of(TextTemplate.class), template);
			ConfigurationNode args = node.getNode("args");
			for (Arg a : clicks.keySet()) {
				args.getNode(a.getName(), "click").setValue(clicks.get(a));
			}
			for (Arg a : hovers.keySet()) {
				args.getNode(a.getName(), "hover").setValue(hovers.get(a));
			}
			type.ifPresent(type -> node.getNode("context").setValue(type.getName()));
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		try {
			return node.getValue(Format.type);
		} catch (ObjectMappingException e) {
			return null;
		}
	}

	/**
	 * Build a format.
	 */
	public static FormatBuilder builder(Group group, String name) {
		return new FormatBuilder(group.getConfig().getNode("format", name), name);
	}

	/**
	 * Set the context.
	 */
	public FormatBuilder withType(FormatContext type) {
		if (type == FormatContexts.DEFAULT) {
			return this;
		}
		this.type = Optional.ofNullable(type);
		return this;
	}

	/**
	 * Add an arg
	 */
	public FormatBuilder withArg(String key) {
		return withArg(key, null, null);
	}

	/**
	 * Add an arg
	 */
	public FormatBuilder withArg(String key, String click, String hover) {
		return withArg(TextTemplate.arg(key), click, hover);
	}

	/**
	 * Add an arg
	 */
	public FormatBuilder withArg(Arg.Builder builder) {
		return withArg(builder, null, null);
	}

	/**
	 * Add an arg
	 */
	public FormatBuilder withArg(Arg.Builder builder, String click, String hover) {
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

	/**
	 * Add text
	 */
	public FormatBuilder withText(Text... texts) {
		for (Text t : texts) {
			template = template.concat(TextTemplate.of(t));
		}
		return this;
	}
}
