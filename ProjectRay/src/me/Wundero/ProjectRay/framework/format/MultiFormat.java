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

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;

import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class MultiFormat extends Format {

	private List<Format> formats = Utils.sl();
	private Mode mode = Mode.SHUFFLE;
	private Random r = new Random();
	private ArrayDeque<Format> unused;

	private Format getNext() {
		switch (mode) {
		case SHUFFLE:
		case SEQUENCE:
			if (unused.isEmpty()) {
				populateDeque();
			}
			return unused.pop();
		default:
			return formats.get(r.nextInt(formats.size()));
		}
	}

	private void populateDeque() {
		unused = formats.stream().collect(Collectors.toCollection(ArrayDeque::new));
	}

	private enum Mode {
		SEQUENCE, RANDOM, SHUFFLE;

		public static Mode getMode(String m) {
			if (m == null) {
				return SHUFFLE;
			}
			m = m.toLowerCase().trim();
			if ("random".startsWith(m)) {
				return RANDOM;
			}
			if ("sequence".startsWith(m)) {
				return SEQUENCE;
			}
			return SHUFFLE;
		}
	}

	public MultiFormat(ConfigurationNode node) {
		super(node);
		mode = Mode.getMode(node.getNode("mode").getString("shuffle"));
		ConfigurationNode subs = node.getNode("formats");
		for (ConfigurationNode f : subs.getChildrenMap().values()) {
			formats.add(Format.create(f, false));
		}
		populateDeque();
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
		return getNext().send(f, args);
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
		return getNext().send(f, data);
	}

}
