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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;

public class EffectTypes {

	private static List<EffectType> types;

	public static final EffectType RAINBOW_TEXT = new EffectType("rainbow", s -> {
		TextColor orig = TextSerializers.FORMATTING_CODE.deserialize(s.getNode("text").getString("")).getColor();
		String g = s.getNode("color").getString();
		if (g == null || g.isEmpty()) {
			throw new NullPointerException("Color must be set!");
		}
		TextColor get = TextUtils.colorFrom(g);
		TextColor t2 = TextUtils.colorFrom(s.getNode("second-color").getString());
		Optional<TextColor> sec = Utils.wrap2(t2, t -> t.isPresent() && !t.get().equals(TextColors.NONE));
		Optional<Integer> del = Utils.wrap2(s.getNode("delay").getInt(10), null, x -> x.isPresent() && x.get() > 0);
		try {
			Text text = s.getNode("text").getValue(TypeToken.of(Text.class));
			return RainbowEffect.create(text, get, sec, del);
		} catch (Exception e) {
			String text = TextUtils.strip(s.getNode("text").getString(""));
			return RainbowEffect.create(text, orig, get, sec, del);
		}
	});
	public static final EffectType SCROLL_TEXT = new EffectType("scroll", s -> {
		Text obj;
		try {
			obj = s.getNode("text").getValue(TypeToken.of(Text.class));
		} catch (Exception e) {
			obj = TextSerializers.FORMATTING_CODE.deserialize(s.getNode("text").getString(""));
		}
		Optional<Integer> len = Utils.wrap2(s.getNode("framelength").getInt(10), null,
				x -> x.isPresent() && x.get() > 0);
		Optional<Integer> spa = Utils.wrap2(s.getNode("spaces").getInt(10), null, x -> x.isPresent() && x.get() > 0);
		Optional<Integer> cha = Utils.wrap2(s.getNode("character-skip").getInt(10), null,
				x -> x.isPresent() && x.get() > 0);
		Optional<Integer> del = Utils.wrap2(s.getNode("delay").getInt(10), null, x -> x.isPresent() && x.get() > 0);
		return TextScrollEffect.create(obj, len, spa, cha, del);
	});

	static {
		types.add(RAINBOW_TEXT);
		types.add(SCROLL_TEXT);
	}

}
