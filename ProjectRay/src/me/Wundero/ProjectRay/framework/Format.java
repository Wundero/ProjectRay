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

import java.util.Optional;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class Format {
	private TextTemplate template;
	private FormatType type;
	private String name;
	private boolean usable = false;
	private Optional<ConfigurationNode> node;

	private Format() {
		setNode(Optional.empty());
	}

	public static Format builder() {
		return new Format();
	}

	public Format withSection(Object... text) {
		if (template == null) {
			template = TextTemplate.of(text);
		} else {
			template = template.concat(TextTemplate.of(text));
		}
		return this;
	}

	public Format build() {
		if (type == null && (name != null && !name.isEmpty())) {
			type = FormatType.fromString(name);
		}
		if (template == null || type == null || name == null || name.isEmpty()) {
			return null;
		}
		usable = true;
		return this;
	}

	public Format withType(FormatType type) {
		return setType(type);
	}

	public Format withTypeFromString(String type) {
		return withType(FormatType.fromString(type));
	}

	public Format withName(String name) {
		return setName(name);
	}

	public Format setName(String name) {
		this.name = name;
		return this;
	}

	public Format(final ConfigurationNode node) {
		this.setNode(Optional.of(node));
		name = node.getKey().toString();
		setType(FormatType.fromString(name));
		Task t = (Task) Task.builder().intervalTicks(20).execute((task) -> {
			try {
				TextTemplate template = node.getNode("format").getValue(TypeToken.of(TextTemplate.class));
				if (template == null) {
					Ray.get().getLogger().info("nooooo");
					return;
				}
				Ray.get().getLogger().info("yay");
				setTemplate(template);
			} catch (Exception e) {
				Utils.printError(e);
			}
			usable = true;
			task.cancel();
		}).submit(Ray.get().getPlugin());
		Ray.get().registerTask(t);
	}

	public boolean usable() {
		return usable;
	}

	public TextTemplate getTemplate() {
		return template;
	}

	public Format setTemplate(TextTemplate template) {
		this.template = template;
		return this;
	}

	public FormatType getType() {
		return type;
	}

	public Format setType(FormatType type) {
		this.type = type;
		return this;
	}

	public Optional<ConfigurationNode> getNode() {
		return node;
	}

	public void setNode(Optional<ConfigurationNode> node) {
		this.node = node;
	}
}
