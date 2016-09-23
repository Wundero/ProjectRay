package me.Wundero.Ray.utils;
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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class OptionalMap<K, V> {

	private Map<K, V> m = Utils.sm();

	public OptionalMap() {
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> e : map.entrySet()) {
			if (e.getValue() == null) {
				continue;
			}
			m.put(e.getKey(), e.getValue());
		}
	}

	public Optional<V> put(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		return Utils.wrap(m.put(key, value));
	}

	public boolean has(K key) {
		Objects.requireNonNull(key);
		return m.containsKey(key);
	}

	public Optional<V> get(K key) {
		Objects.requireNonNull(key);
		return Utils.wrap(m.get(key));
	}

	public int size() {
		return m.size();
	}

	public boolean isEmpty() {
		return m.isEmpty();
	}

	public boolean containsKey(K key) {
		if (key == null) {
			return false;
		}
		return has(key);
	}

	public boolean containsValue(V value) {
		if (value == null) {
			return false;
		}
		return m.containsValue(value);
	}

	public Optional<V> remove(K key) {
		if (!has(key)) {
			return Optional.empty();
		}
		return Utils.wrap(m.remove(key));
	}

	public void clear() {
		m.clear();
	}

	public Set<K> keySet() {
		return m.keySet();
	}

	public Collection<V> values() {
		return m.values();
	}

	public Set<Entry<K, V>> entrySet() {
		return m.entrySet();
	}

}
