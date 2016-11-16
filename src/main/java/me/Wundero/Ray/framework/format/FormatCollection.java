package me.Wundero.Ray.framework.format;
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.channel.MessageReceiver;

import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;

/**
 * Collection of formats. Has some built in iteration methods.
 */
public class FormatCollection {

	private List<Format> formats = Utils.sl();

	/**
	 * Get the internal list for iteration.
	 */
	public List<Format> get() {
		return formats;
	}

	/**
	 * Set the internal list.
	 */
	public void set(List<Format> formats) {
		this.formats = formats;
	}

	/**
	 * Create an empty format collection.
	 */
	public FormatCollection() {
	}

	/**
	 * Create a format collection with the specified formats.
	 */
	public FormatCollection(Format... formats) {
		this.formats = Utils.sl(formats);
	}

	/**
	 * Create a format collection with the specified formats.
	 */
	public FormatCollection(Collection<Format> formats) {
		this.formats = Utils.sl(formats, true);
	}

	/**
	 * Get the size of the collection.
	 */
	public int size() {
		return formats.size();
	}

	/**
	 * Remove a format.
	 */
	public boolean remove(Format f) {
		return formats.remove(f);
	}

	/**
	 * Remove a format.
	 */
	public Format remove(int index) {
		return formats.remove(index);
	}

	/**
	 * Add a format.
	 */
	public boolean add(Format f) {
		return formats.add(f);
	}

	/**
	 * Add a format at the index.
	 */
	public void add(Format f, int i) {
		formats.add(i, f);
	}

	/**
	 * Add formats.
	 */
	public void addAll(Collection<? extends Format> formats) {
		this.formats.addAll(formats);
	}

	/**
	 * Get internal formats for the class.
	 */
	public <T extends Format> List<T> getInternals(Class<T> clazz, Optional<Integer> page) {
		List<T> out = Utils.al();
		for (Format f : formats) {
			if (clazz.isInstance(f)) {
				out.add(clazz.cast(f));
			}
			if (f.hasInternal(clazz, page)) {
				f.getInternal(clazz, page).ifPresent(fm -> out.add(fm));
			}
		}
		return out;
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, Map<String, Object> args, Object o, boolean irrelevant) {
		return sendAll(target, args, Utils.wrap(o));
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, ParsableData data, Object o, boolean irrelevant) {
		return sendAll(target, data, Utils.wrap(o));
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, Map<String, Object> args, Optional<Object> sender) {
		int count = 0;
		for (Format f : this.formats) {
			if (f.send(target, args, sender)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, ParsableData data, Optional<Object> sender) {
		int count = 0;
		for (Format f : this.formats) {
			if (f.send(target, data, sender)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return whether the collection is empty.
	 */
	public boolean isEmpty() {
		return formats.isEmpty();
	}

	/**
	 * Check to see if the list contains a format.
	 */
	public boolean contains(Object o) {
		return formats.contains(o);
	}

	/**
	 * Iterator.
	 */
	public Iterator<Format> iterator() {
		return formats.iterator();
	}

	/**
	 * Convert to an array.
	 */
	public Object[] toArray() {
		return formats.toArray();
	}

	/**
	 * Convert to an array.
	 */
	public <T> T[] toArray(T[] a) {
		return formats.toArray(a);
	}

	/**
	 * Check to see if the collection contains another collection
	 */
	public boolean containsAll(Collection<?> c) {
		return formats.containsAll(c);
	}

	/**
	 * Add all formats to an index.
	 */
	public boolean addAll(int index, Collection<? extends Format> c) {
		return formats.addAll(index, c);
	}

	/**
	 * Remove all specified formats.
	 */
	public boolean removeAll(Collection<?> c) {
		return formats.removeAll(c);
	}

	/**
	 * Retain all specified formats.
	 */
	public boolean retainAll(Collection<?> c) {
		return formats.retainAll(c);
	}

	/**
	 * Clear the collection.
	 */
	public void clear() {
		formats.clear();
	}

	/**
	 * Get a format.
	 */
	public Format get(int index) {
		return formats.get(index);
	}

	/**
	 * Set a format.
	 */
	public Format set(int index, Format element) {
		return formats.set(index, element);
	}

	/**
	 * Add a format.
	 */
	public void add(int index, Format element) {
		formats.add(index, element);
	}

	/**
	 * Find the index of a format.
	 */
	public int indexOf(Object o) {
		return formats.indexOf(o);
	}

	/**
	 * Find the last index of a format.
	 */
	public int lastIndexOf(Object o) {
		return formats.lastIndexOf(o);
	}

	/**
	 * List iterator.
	 */
	public ListIterator<Format> listIterator() {
		return formats.listIterator();
	}

	/**
	 * List iterator.
	 */
	public ListIterator<Format> listIterator(int index) {
		return formats.listIterator(index);
	}

	/**
	 * Get a sublist between two indices.
	 */
	public List<Format> subList(int fromIndex, int toIndex) {
		return formats.subList(fromIndex, toIndex);
	}

}
