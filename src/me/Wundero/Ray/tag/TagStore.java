package me.Wundero.Ray.tag;
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

import org.apache.commons.lang3.Validate;

import me.Wundero.Ray.utils.Utils;

public class TagStore {

	private Map<String, Tag<?>> tags = Utils.sm();

	void register(Tag<?> t) {
		if (has(t.getName())) {
			return;
		}
		this.tags.put(t.getName(), t);
	}

	public boolean has(String name) {
		return tags.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public <T, R extends Tag<T>> Optional<R> get(String name, T verifiable, Class<R> tagClass) {
		Tag<?> t = get(name).orElse(null);
		if (t == null) {
			return Optional.empty();
		}
		if (tagClass.isAssignableFrom(t.getClass())) {
			if (!t.verify(verifiable)) {
				return Optional.empty();
			}
			return Optional.ofNullable((R) t);
		}
		return Optional.empty();
	}

	public Optional<Tag<?>> get(String name) {
		Validate.notEmpty(name);
		return Optional.ofNullable(tags.get(name));
	}

}
