package me.Wundero.Ray.framework.player.name;

import java.util.Optional;

import org.spongepowered.api.text.Text;

import me.Wundero.Ray.utils.Utils;

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

public class DisplayName implements Nicknamable, Prefixable, Suffixable {

	private final Text original;
	private Optional<Text> nick, prefix, suffix;

	public DisplayName(Text original) {
		this.original = original;
	}

	public void offer(Text prefix, Text name, Text suffix) {
		prefix(prefix);
		nickname(name);
		suffix(suffix);
	}

	@Override
	public void clear() {
		offer(null, original(), null);
	}

	@Override
	public boolean suffix(Text suffix) {
		Optional<Text> t = Utils.wrap(suffix);
		if (t.isPresent()) {
			this.suffix = t;
		}
		return this.suffix.isPresent();
	}

	@Override
	public boolean prefix(Text prefix) {
		Optional<Text> t = Utils.wrap(prefix);
		if (t.isPresent()) {
			this.prefix = t;
		}
		return this.prefix.isPresent();
	}

	@Override
	public boolean nickname(Text nickname) {
		Optional<Text> t = Utils.wrap(nickname);
		if (t.isPresent()) {
			this.nick = t;
		}
		return this.nick.isPresent();
	}

	@Override
	public Text original() {
		return original;
	}

	@Override
	public Optional<Text> nickname() {
		return nick;
	}

	@Override
	public Optional<Text> suffix() {
		return suffix;
	}

	@Override
	public Optional<Text> prefix() {
		return prefix;
	}

}
