package me.Wundero.Ray.config;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate.Arg;

import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.location.FormatLocation;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

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

public class DefaultFormat {

	private String name;
	private List<Object> parts;
	private FormatContext type;
	private Optional<FormatLocation> loc = Optional.empty();
	private Optional<Consumer<ConfigurationNode>> ld = Optional.empty();

	public DefaultFormat(String name, FormatContext type) {
		this.name = name;
		this.parts = Utils.sl();
		this.type = type;
	}

	public DefaultFormat(String name) {
		this(name, FormatContext.fromString(name));
	}

	public DefaultFormat withLoc(FormatLocation loc) {
		this.loc = Utils.wrap(loc);
		return this;
	}

	public DefaultFormat withLocData(Consumer<ConfigurationNode> ld) {
		this.ld = Utils.wrap(ld);
		return this;
	}

	public DefaultFormat with(Object... o) {
		for (Object a : o) {
			parts.add(a);
		}
		return this;
	}

	public Template.Builder.GroupBuilder applyTo(Template.Builder.GroupBuilder b) {
		Template.Builder.GroupBuilder.FormatBuilder bb = b.withFormat(name);
		for (Object o : parts) {
			if (o instanceof DefaultArg) {
				bb.withArg((DefaultArg) o);
			} else if (o instanceof Text) {
				bb.withText((Text) o);
			} else if (o instanceof Arg.Builder) {
				bb.withArg((Arg.Builder) o);
			} else {
				bb.withText(o.toString());
			}
		}
		bb.withType(type);
		loc.ifPresent(l -> {
			bb.withLoc(l);
			ld.ifPresent(ldd -> bb.withLocData(ldd));
		});
		return bb.build();
	}

}
