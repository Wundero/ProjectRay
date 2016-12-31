package me.Wundero.Ray;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
 * Class used to store transient information. More of a random utility than
 * anything used a lot. Casts are not required. Class is also synchronous to
 * prevent thread errors.
 */

public abstract class DataHolder {

	/**
	 * Stores the data of the object as a key-value store.
	 */
	protected Map<String, Object> data = Utils.sm();

	/**
	 * Internal UUID for equals testing
	 */
	protected UUID uuid = UUID.randomUUID();

	@Override
	public boolean equals(Object o) {
		return o instanceof DataHolder && ((DataHolder) o).uuid.equals(uuid);
	}

	/**
	 * Push a map onto the data set.
	 */
	public synchronized void putAll(Map<String, Object> values) {
		data.putAll(values);
	}

	/**
	 * Get an object from a key.
	 */
	public synchronized <T> Optional<T> getData(String key, Class<T> clazz) {
		if (!hasData(key)) {
			return Optional.empty();
		}
		try {
			Object o = data.get(key);
			if (!clazz.isInstance(o)) {
				return Optional.empty();
			}
			return Utils.wrap(clazz.cast(o));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Get an object from a key. If the returning object is null or the map does
	 * not contain the key, the default value is returned.
	 */
	public synchronized <T> T getData(String key, Class<T> clazz, T def) {
		if (!hasData(key)) {
			return def;
		}
		T out = getData(key, clazz).orElse(def);
		return out == null ? def : out;
	}

	/**
	 * Returns whether the data exists.
	 */
	public synchronized boolean hasData(String key) {
		return data.containsKey(key);
	}

	/**
	 * Delete the value stored by a key. Returns the value removed.
	 */
	public synchronized Optional<Object> removeData(String key) {
		return Utils.wrap(data.remove(key));
	}

	/**
	 * Push datum onto the set under a key.
	 */
	public synchronized <T> Optional<Object> putData(String key, T value) {
		Object o = data.put(key, value);
		return Utils.wrap(o);
	}

	/**
	 * Wipes all stored data
	 */
	public synchronized void clearData() {
		data.clear();
	}

	/**
	 * Return the internal UUID.
	 */
	public synchronized UUID getUUID() {
		return uuid;
	}

	/**
	 * Set the internal UUID.
	 */
	public synchronized void setUUID(UUID u) {
		this.uuid = u;
	}

}
