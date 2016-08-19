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
import java.util.function.Function;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.animation.Animation;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class AnimatedFormat extends Format {
	private Map<TextTemplate, Integer> frameWithDelay = Utils.sm();

	public AnimatedFormat(ConfigurationNode node) {
		super(node);
		ConfigurationNode frames = node.getNode("frames");
		Map<TextTemplate, Integer> t = Utils.sm();
		// TODO ensure proper order
		for (ConfigurationNode frame : frames.getChildrenMap().values()) {
			TextTemplate f = null;
			try {
				f = frame.getNode("frame").getValue(TypeToken.of(TextTemplate.class));
			} catch (ObjectMappingException e) {
				e.printStackTrace();
				continue;
			}
			int d = frame.getNode("stay").getInt(10);
			if (f != null) {
				t.put(f, d);
			}
		}
		frameWithDelay = t;
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		Animation<TextTemplate> anim = new Animation<TextTemplate>(Utils.sl(frameWithDelay.keySet()), (template) -> {
			if (!this.s(f, args, template)) {
				return -1;
			}
			return frameWithDelay.get(template);
		}, (template) -> {
			return template != null;
		});
		anim.start();
		return true;
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		Animation<TextTemplate> anim = new Animation<TextTemplate>(Utils.sl(frameWithDelay.keySet()), (template) -> {
			if (!this.s(f, data, template)) {
				return -1;
			}
			return frameWithDelay.get(template);
		}, (template) -> {
			return template != null;
		});
		anim.start();
		return true;
	}

}
