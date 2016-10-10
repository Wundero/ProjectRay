package me.Wundero.Ray.animation.effect;
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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import me.Wundero.Ray.utils.TextUtils;

public class RainbowEffect extends Effect<Text> {

	// highlight part of text
	// ex: 1: abc 2 Abc 3 aBc 4 abC

	public static RainbowEffect create(String text, TextColor original, TextColor main, Optional<TextColor> second,
			Optional<Integer> delay) {
		RainbowEffect e = new RainbowEffect(Text.of(text), (t, i) -> {
			if (!second.isPresent()) {
				if (i == 0) {
					return Text.of(original, text);
				}
				if (i == text.length()) {
					throw new NullPointerException("extra");
				}
				int f = i - 1;
				String a = text.charAt(f) + "";
				String b = text.substring(0, f);
				String c = text.substring(f + 1);
				return Text.of(original, b).concat(Text.of(main, a)).concat(Text.of(original, c));
			} else {
				TextColor c = second.get();
				if (i == 0) {
					return Text.of(original, text);
				}
				if (i == text.length() + 2) {
					throw new NullPointerException("extra");
				}
				if (i == 1) {
					return Text.of(c, text.charAt(0)).concat(Text.of(original, text.substring(1)));
				}
				if (i == 2) {
					return Text.of(main, text.charAt(0)).concat(Text.of(c, text.charAt(1)))
							.concat(Text.of(original, text.substring(2)));
				}
				if (i == text.length() + 1) {
					return Text.of(original, text.substring(0, text.length() - 1))
							.concat(Text.of(c, text.charAt(text.length() - 1)));
				}
				if (i == text.length()) {
					return Text.of(original, text.substring(0, text.length() - 2))
							.concat(Text.of(c, text.charAt(text.length() - 2)))
							.concat(Text.of(main, text.charAt(text.length() - 1)));
				}
				int f = i - 1;
				String aX = text.substring(0, f - 1);
				String bX = text.charAt(f - 1) + "";
				String cX = text.charAt(f) + "";
				String dX = text.charAt(f + 1) + "";
				String eX = text.substring(f + 2);
				return Text.of(original, aX).concat(Text.of(c, bX)).concat(Text.of(main, cX)).concat(Text.of(c, dX))
						.concat(Text.of(original, eX));
			}
		}, text.length() + (second.isPresent() ? 2 : 0), delay.orElse(0));
		return e;
	}

	public static RainbowEffect create(Text t, TextColor main, Optional<TextColor> secondary, Optional<Integer> delay) {
		return create(TextUtils.getContent(t, false), t.getColor(), main, secondary, delay);
	}

	private RainbowEffect(Text obj, BiFunction<Text, Integer, Text> mod, int f, int d) {
		super(obj, mod, f, d);
	}

}
