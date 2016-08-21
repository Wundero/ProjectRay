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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;

import me.Wundero.ProjectRay.animation.Animation;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class AnimatedFormat extends Format {
	private Map<Format, Integer> frameWithDelay = Utils.sm();
	private List<Format> inOrder = Utils.sl();

	private static class Frame {
		private Format v;
		private int o;

		public Frame(Format f, int i) {
			setO(i);
			setV(f);
		}

		public Format getV() {
			return v;
		}

		public void setV(Format v) {
			this.v = v;
		}

		public int getO() {
			return o;
		}

		public void setO(int o) {
			this.o = o;
		}

	}

	public AnimatedFormat(ConfigurationNode node) {
		super(node);
		ConfigurationNode frames = node.getNode("frames");
		Map<Format, Integer> t = Utils.sm();
		List<Frame> framez = Utils.sl();
		for (ConfigurationNode frame : frames.getChildrenMap().values()) {
			Format f = null;
			try {
				f = Format.create(frame);
				if (f == null || f instanceof AnimatedFormat) {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			int n = frame.getNode("number").getInt(-1);
			int d = frame.getNode("stay").getInt(10);
			if (f != null) {
				t.put(f, d);
				framez.add(new Frame(f, n));
			}
		}
		framez.sort((frame1, frame2) -> {
			return frame1.getO() - frame2.getO();
		});
		inOrder = Utils.sl(framez.stream().map(frame -> frame.getV()).collect(Collectors.toList()));
		frameWithDelay = t;
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (!template.send(f, args)) {
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
		Animation<Format> anim = new Animation<Format>(inOrder, (template) -> {
			if (!template.send(f, data)) {
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
