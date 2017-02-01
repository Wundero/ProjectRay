package me.Wundero.Ray.config;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.framework.Group;
import me.Wundero.Ray.framework.Groups;
import me.Wundero.Ray.framework.args.Argument;
import me.Wundero.Ray.framework.execute.TemplateExecutable;
import me.Wundero.Ray.framework.format.StaticFormat;
import me.Wundero.Ray.framework.format.context.FormatContexts;
import me.Wundero.Ray.framework.format.location.FormatLocations;

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

public class DefaultConfCreator {

	public Groups getGroupsDef() {
		Groups out = new Groups();
		Group g = new Group();
		StaticFormat f = new StaticFormat();
		f.setContext(FormatContexts.CHAT);
		f.setLocation(FormatLocations.CHAT);
		f.setName("chat");
		f.addArg("displayname", new Argument("displayname", Text.of(TextColors.AQUA, "{displayname}")));
		f.addExe("chat", new TemplateExecutable(TextTemplate.of(TextTemplate.arg("displayname"),
				Text.of(TextColors.GRAY, ": "), TextTemplate.arg("message").color(TextColors.GRAY))));
		g.addFormat(f);
		// TODO replace contexts with triggers?
		// g.addTrigger("chat", new EventTrigger("chat"));
		out.addGroup("default", g);
		return out;
	}

}
