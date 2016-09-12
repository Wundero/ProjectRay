package me.Wundero.ProjectRay.animation.effect;
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
import java.util.function.BiFunction;

public class ScrollEffect extends Effect<String> {

	private ScrollEffect(String obj) {
		this(obj, obj.length() < 16 ? obj.length() : 16);
	}

	private ScrollEffect(String obj, int framelength) {
		this(obj, g(framelength), framelength);
	}

	private ScrollEffect(String obj, int framelength, int spaces) {
		this(obj, g(framelength), spaces, 1);
	}

	private ScrollEffect(String obj, int framelength, int spaces, int charchange) {
		this(obj, g(framelength), g(spaces, false), charchange, 2);
	}

	public static ScrollEffect create(String o, Optional<Integer> f, Optional<Integer> s, Optional<Integer> c,
			Optional<Integer> d) {
		int ft = f.orElse(o.length() < 16 ? o.length() : 16);
		if (d.isPresent()) {
			return new ScrollEffect(o, g(ft), g(s.orElse(ft), false), c.orElse(1), d.get());
		} else if (c.isPresent()) {
			return new ScrollEffect(o, ft, s.orElse(ft), c.get());
		} else if (s.isPresent()) {
			return new ScrollEffect(o, ft, s.get());
		} else if (f.isPresent()) {
			return new ScrollEffect(o, ft);
		}
		return new ScrollEffect(o);
	}

	private static int g(int i) {
		if (i <= 0) {
			throw new IllegalArgumentException("framelength must be greater than 0!");
		}
		return i;
	}

	private static int g(int i, boolean k) {
		if (i < 0) {
			throw new IllegalArgumentException("spaces must be greater than or equal to 0!");
		}
		return i;
	}

	private static String buildforspaces(String s, int i) {
		String out = "%s%s%s";
		String sa = "";
		for (int f = 0; f < i; f++) {
			sa += " ";
		}
		if (sa.isEmpty()) {
			sa = s;
		}
		out = String.format(out, sa, s, sa);
		return out;
	}

	// scrolling text

	private ScrollEffect(final String obj, int framelength, int spaces, int charchange, int delay) {
		this(buildforspaces(obj, spaces), (s, i) -> {
			if (i % charchange != 0) {
				throw new NullPointerException("extra");
			}
			g(framelength);
			g(spaces, false);
			if (i == 0 && spaces != 0) {
				throw new NullPointerException("extra");
			}
			return s.substring(i, i + framelength);
		}, obj.length() + spaces, delay);
	}

	private ScrollEffect(String obj, BiFunction<String, Integer, String> mod, int f, int d) {
		super(obj, mod, f, d);
	}

}
