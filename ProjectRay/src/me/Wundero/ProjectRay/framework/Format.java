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

import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Format {
	private TextTemplate template;
	private FormatType type;
	private String name;
	private boolean usable = false;

	private Format() {

	}

	public static Format builder() {
		return new Format();
	}

	public Format withSection(Object... text) {
		if (template == null) {
			template = TextTemplate.of(text);
		} else {
			template.concat(TextTemplate.of(text));
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

	public Format withName(String name) {
		return setName(name);
	}

	public Format setName(String name) {
		this.name = name;
		return this;
	}

	public Format(ConfigurationNode node) {
		name = node.getKey().toString();
		setType(FormatType.fromString(name));
		try {
			setTemplate(node.getNode("format").getValue(TypeToken.of(TextTemplate.class)));
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		usable = true;
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
}
