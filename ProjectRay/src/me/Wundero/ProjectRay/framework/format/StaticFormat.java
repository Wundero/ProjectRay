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

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class StaticFormat extends Format {
	private TextTemplate template;

	public StaticFormat(ConfigurationNode node) {
		super(node);
		if (node.getNode("simple").isVirtual()) {
			Task t = (Task) Task.builder().intervalTicks(20).execute((task) -> {
				try {
					TextTemplate template = node.getNode("format").getValue(TypeToken.of(TextTemplate.class));
					if (template == null) {
						return;
					}
					setTemplate(template);
				} catch (Exception e) {
					Utils.printError(e);
				}
				usable = true;
				task.cancel();
				Ray.get().finishFormatTask(task);
			}).submit(Ray.get().getPlugin());
			Ray.get().registerFormatTask(t);
		} else {
			String simple = node.getNode("simple").getString();
			node.getNode("simple").setValue(null);
			setTemplate(Utils.parse(simple, true));
			usable = template != null;
			if (usable) {
				try {
					node.getNode("format").setValue(TypeToken.of(TextTemplate.class), template);
				} catch (ObjectMappingException e) {
					Utils.printError(e);
				}
			}
		}
	}

	private void setTemplate(TextTemplate template) {
		this.template = template;
	}

	public Optional<TextTemplate> getTemplate() {
		if (usable) {
			return Optional.ofNullable(template);
		}
		return Optional.empty();
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		if (!getTemplate().isPresent()) {
			return false;
		}
		return this.s(f, args, getTemplate().get());
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		if (!getTemplate().isPresent()) {
			return false;
		}
		return this.s(f, data, getTemplate().get());
	}

}
