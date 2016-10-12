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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import me.Wundero.Ray.utils.TextUtils;

public class TextScrollEffect extends Effect<Text> {

	private TextScrollEffect(Text obj, BiFunction<Text, Integer, Text> mod, int f, int d) {
		super(obj, mod, (t, p) -> t, f, d);// again, text parsing?
	}

	private static String bs(int i) {
		String s = "";
		for (int x = 0; x < i; x++) {
			s += " ";
		}
		return s;
	}

	public static TextScrollEffect create(Text o, Optional<Integer> framelength, Optional<Integer> spaces,
			Optional<Integer> charchange, Optional<Integer> delay) {
		final int f = framelength.orElse(16);
		final int s = spaces.orElse(f);
		final int d = delay.orElse(10);
		final int c = charchange.orElse(1);
		if (f <= 0) {
			throw new IllegalArgumentException("Frame length must be greater than 0");
		}
		if (s < 0) {
			throw new IllegalArgumentException("Spaces must be greater than or equal to 0");
		}
		String sp = bs(s);
		Text spt = Text.of(sp);
		Text officialObject = null;
		if (sp.isEmpty()) {
			o.concat(o).concat(o);
		} else {
			spt.concat(o).concat(spt);
		}
		BiFunction<Text, Integer, Text> mod = (text, frame) -> {
			if (frame % c != 0) {
				throw new NullPointerException("Frame is unnecessary.");
			}
			if (frame == 0 && s != 0) {
				throw new NullPointerException("Frame is unnecessary.");
			}
			return TextUtils.substring(text, frame, frame + f);
		};
		return new TextScrollEffect(officialObject, mod, TextUtils.length(officialObject) + s, d);
	}

	@Override
	public boolean send(BiFunction<Text, Player, Boolean> sender, Text obj, Player p) {
		return sender.apply(obj, p);
	}

}
