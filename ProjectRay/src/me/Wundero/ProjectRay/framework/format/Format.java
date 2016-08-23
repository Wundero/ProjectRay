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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Format {
	private FormatType type;
	private String name;
	protected boolean usable = false;
	private Optional<ConfigurationNode> node;

	public abstract boolean send(Function<Text, Boolean> f, Map<String, Object> args);

	public abstract boolean send(Function<Text, Boolean> f, ParsableData data);

	protected boolean s(Function<Text, Boolean> f, Map<String, Object> a, TextTemplate t) {
		boolean b = false;
		try {
			b = f.apply(get(t, a));
		} catch (Exception e) {
			b = false;
		}
		return b;
	}

	protected boolean s(Function<Text, Boolean> f, ParsableData d, TextTemplate t) {
		boolean b = false;
		try {
			b = f.apply(get(t, d));
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}

	protected Text get(TextTemplate t, Map<String, Object> data) {
		return t.apply(data).build();
	}

	protected Text get(TextTemplate t, ParsableData data) {
		return t.apply(parse(t, data)).build();
	}

	protected Map<String, Object> parse(TextTemplate t, ParsableData data) {
		if (data == null) {
			return Utils.sm();
		}
		return Ray.get().setVars(data.getKnown().orElse(Utils.sm()), t, data.getSender(), data.getRecipient(),
				data.getObserver(), Optional.of(this), data.isClickHover());
	}

	public String getName() {
		return name;
	}

	public Format setName(String name) {
		this.name = name;
		return this;
	}

	private static FormatType getDaType(ConfigurationNode n) {
		if (n.getNode("type").isVirtual()) {
			return FormatType.fromString(n.getKey().toString());
		} else {
			return FormatType.fromString(n.getNode("type").getString());
		}
	}

	public static Format create(ConfigurationNode node, boolean allowNonstatic) {
		if (allowNonstatic) {
			return create(node);
		} else {
			return new StaticFormat(node);
		}
	}

	public static Format create(ConfigurationNode node) {
		if (node == null || node.isVirtual()) {
			return null;
		}
		if (getDaType(node) == null || !getDaType(node).isAnimated()) {
			return statormulti(node);
		} else {
			if (node.getNode("frames").isVirtual()) {
				return statormulti(node);
			}
			return new AnimatedFormat(node);
		}
	}

	private static Format statormulti(ConfigurationNode node) {
		if (node.getNode("formats").isVirtual()) {
			return new StaticFormat(node);
		}
		return new MultiFormat(node);
	}

	public Format(final ConfigurationNode node) {
		this.setNode(Optional.of(node));
		name = node.getKey().toString();
		setType(node.getNode("type").isVirtual() ? FormatType.fromString(name)
				: FormatType.fromString(node.getNode("type").getString()));
		node.getNode("type").setValue(type.getName());
		// forces type to be present
	}

	public boolean usable() {
		return usable;
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
