package me.Wundero.ProjectRay.config;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate.Arg;

import me.Wundero.ProjectRay.framework.format.type.FormatType;
import me.Wundero.ProjectRay.utils.Utils;

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
	private FormatType type;

	public DefaultFormat(String name, FormatType type) {
		this.name = name;
		this.parts = Utils.sl();
		this.type = type;
	}

	public DefaultFormat(String name) {
		this(name, FormatType.fromString(name));
	}

	public DefaultFormat with(Object o) {
		parts.add(o);
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
		return bb.build();
	}

}
