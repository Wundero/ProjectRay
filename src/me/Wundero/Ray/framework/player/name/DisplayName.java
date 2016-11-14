package me.Wundero.Ray.framework.player.name;

import java.util.Optional;
import java.util.function.Function;

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

/**
 * Represents a textual displayname for a player containing a prefix, nickname
 * and suffix.
 */
public class DisplayName implements Nicknamable, Prefixable, Suffixable {

	private final Text original;
	private Optional<Text> nick, prefix, suffix;

	/**
	 * Create a displayname handler with an original name
	 */
	public DisplayName(Text original) {
		this.original = original;
	}

	/**
	 * Apply the displayname to a functional applicant. Optional separator
	 * string that will split prefix/name/suffix. To not use separator, set it
	 * to null or "".
	 */
	public boolean apply(Function<Text, Boolean> applicant, String separator) {
		Text.Builder b = Text.builder("");
		Optional<Text> sep = Utils.wrap(Text.of(separator), separator != null, !separator.isEmpty());
		prefix.ifPresent(t -> {
			b.append(t);
			sep.ifPresent(a -> b.append(a));
		});
		b.append(nick.orElse(original));
		suffix.ifPresent(t -> {
			sep.ifPresent(a -> b.append(a));
			b.append(t);
		});
		return applicant.apply(b.build());
	}

	/**
	 * Return the displayname. Optional separator string that will split
	 * prefix/name/suffix. To not use separator, set it to null or "".
	 */
	public Text getDisplayName(String separator) {
		Text.Builder b = Text.builder("");
		Optional<Text> sep = Utils.wrap(Text.of(separator), separator != null, !separator.isEmpty());
		prefix.ifPresent(t -> {
			b.append(t);
			sep.ifPresent(a -> b.append(a));
		});
		b.append(nick.orElse(original));
		suffix.ifPresent(t -> {
			sep.ifPresent(a -> b.append(a));
			b.append(t);
		});
		return b.build();
	}

	/**
	 * Offer a displayname. Parameter is nullable.
	 */
	public boolean offer(Text displayname) {
		return offer(Text.EMPTY, displayname, Text.EMPTY);
	}

	/**
	 * Offer a displayname. All parameters are nullable.
	 */
	public boolean offer(Text prefix, Text name, Text suffix) {
		boolean r = prefix(prefix);
		if (r == false) {
			return false;
		}
		r = r && nickname(name);
		if (r == false) {
			return false;
		}
		r = r && suffix(suffix);
		return r;
	}

	/**
	 * Clear the displayname and set it to the original.
	 */
	@Override
	public void clear() {
		offer(null, original(), null);
	}

	/**
	 * Apply a suffix.
	 */
	@Override
	public boolean suffix(Text suffix) {
		this.suffix = Utils.wrap(suffix);
		return this.suffix.isPresent();
	}

	/**
	 * Apply a prefix.
	 */
	@Override
	public boolean prefix(Text prefix) {
		this.prefix = Utils.wrap(prefix);
		return this.prefix.isPresent();
	}

	/**
	 * Apply a nickname.
	 */
	@Override
	public boolean nickname(Text nickname) {
		this.nick = Utils.wrap(nickname);
		return this.nick.isPresent();
	}

	/**
	 * @return the original name.
	 */
	@Override
	public Text original() {
		return original;
	}

	/**
	 * @return the nickname.
	 */
	@Override
	public Optional<Text> nickname() {
		return nick;
	}

	/**
	 * @return the suffix.
	 */
	@Override
	public Optional<Text> suffix() {
		return suffix;
	}

	/**
	 * @return the prefix.
	 */
	@Override
	public Optional<Text> prefix() {
		return prefix;
	}

}
