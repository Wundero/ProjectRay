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
import java.util.stream.Collectors;

/**
 * Map wrapper that uses optionals rather than nulls. Strict non-null error
 * throwing is used. Slightly more generic and more strict that a standard map
 */
public class OptionalMap<K, V> {

	private Map<K, V> m = Utils.sm();

	public OptionalMap() {
	}

	/**
	 * Load all key-value entries from an existing map
	 */
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> e : map.entrySet()) {
			if (e.getValue() == null || e.getKey() == null) {
				continue;
			}
			m.put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Add an entry
	 */
	public Optional<V> put(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		return Utils.wrap(m.put(key, value));
	}

	/**
	 * Check to see if an entry exists
	 */
	public boolean has(K key) {
		Objects.requireNonNull(key);
		return m.containsKey(key);
	}

	/**
	 * Get an object from the map
	 */
	public Optional<V> get(K key) {
		Objects.requireNonNull(key);
		if (!has(key)) {
			return Optional.empty();
		}
		return Utils.wrap(m.get(key));
	}

	/**
	 * Get the size of the map
	 */
	public int size() {
		return m.size();
	}

	/**
	 * Get whether the map is empty or not
	 */
	public boolean isEmpty() {
		return m.isEmpty();
	}

	/**
	 * Check to see if the map contains a key. If the key is null, false is
	 * returned.
	 */
	public boolean containsKey(K key) {
		if (key == null) {
			return false;
		}
		return has(key);
	}

	/**
	 * Check to see if the map contains a value. If the value is null, false is
	 * returned.
	 */
	public boolean containsValue(V value) {
		if (value == null) {
			return false;
		}
		return m.containsValue(value);
	}

	/**
	 * Remove the value mapped to a key.
	 */
	public Optional<V> remove(K key) {
		if (!has(key)) {
			return Optional.empty();
		}
		return Utils.wrap(m.remove(key));
	}

	/**
	 * Empty the map
	 */
	public void clear() {
		m.clear();
	}

	/**
	 * Get the set of keys
	 */
	public Set<K> keySet() {
		return m.keySet().stream().filter(v -> v != null).collect(Collectors.toSet());
	}

	/**
	 * Get the collection of values
	 */
	public Collection<V> values() {
		return m.values().stream().filter(v -> v != null).collect(Collectors.toSet());
	}

	/**
	 * Get the set of entries
	 */
	public Set<Entry<K, V>> entrySet() {
		return m.entrySet().stream().filter(e -> e != null && e.getValue() != null && e.getKey() != null)
				.collect(Collectors.toSet());
	}

}
