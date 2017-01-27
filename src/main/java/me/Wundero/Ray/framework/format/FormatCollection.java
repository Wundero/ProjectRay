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
import java.util.UUID;

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
		this.formats = Utils.sl(formats, true);
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
		set(Utils.al(formats));
	}

	/**
	 * Create a format collection with the specified formats.
	 */
	public FormatCollection(Collection<Format> formats) {
		set(Utils.al(formats, true));
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
		return formats.remove(f.getName());
	}

	/**
	 * Add a format.
	 */
	public boolean add(Format f) {
		return formats.add(f);
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, Map<String, Object> args, Object o, UUID u, boolean broadcast,
			boolean irrelevant) {
		return sendAll(target, args, Utils.wrap(o), Utils.wrap(u), broadcast);
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, ParsableData data, Object o, UUID u, boolean broadcast,
			boolean irrelevant) {
		return sendAll(target, data, Utils.wrap(o), Utils.wrap(u), broadcast);
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, Map<String, Object> args, Optional<Object> sender, Optional<UUID> u,
			boolean broadcast) {
		int count = 0;
		for (Format f : formats) {
			if (f.send(target, args, sender, u, broadcast)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Send to all formats.
	 */
	public int sendAll(MessageReceiver target, ParsableData data, Optional<Object> sender, Optional<UUID> u,
			boolean broadcast) {
		int count = 0;
		for (Format f : formats) {
			if (f.send(target, data, sender, u, broadcast)) {
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
	 * Clear the collection.
	 */
	public void clear() {
		formats.clear();
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
