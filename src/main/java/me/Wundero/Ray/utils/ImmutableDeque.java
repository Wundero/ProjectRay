package me.Wundero.Ray.utils;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

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
 * Immutable implementation of Deque. Used only for ArRayDeque to allow for
 * proper iter
 */
public class ImmutableDeque<E> implements Deque<E> {

	private Deque<E> wrapped;
	Object[] elements;
	int head = 0;
	int tail;

	public ImmutableDeque(Deque<E> wrap) {
		this.wrapped = wrap;
		elements = wrap.toArray();
		tail = wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return wrapped.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrapped.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrapped.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeFirst() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeLast() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pollFirst() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pollLast() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E getFirst() {
		return wrapped.getFirst();
	}

	@Override
	public E getLast() {
		return wrapped.getLast();
	}

	@Override
	public E peekFirst() {
		return wrapped.peekFirst();
	}

	@Override
	public E peekLast() {
		return wrapped.peekLast();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E poll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E element() {
		return wrapped.element();
	}

	@Override
	public E peek() {
		return wrapped.peek();
	}

	@Override
	public void push(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pop() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return wrapped.contains(o);
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public Iterator<E> iterator() {
		return new UnmodifiableDeqIterator();
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new UnmodifiableDecreasingIterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		return new UnmodifiableDeqSpliterator<E>(this, -1, -1);
	}

	private class UnmodifiableDeqIterator implements Iterator<E> {
		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		private int cursor = head;

		private Object[] lements = new Object[elements.length];

		public UnmodifiableDeqIterator() {
			System.arraycopy(elements, 0, lements, 0, elements.length);
		}

		/**
		 * Tail recorded at construction (also in remove), to stop iterator and
		 * also to check for comodification.
		 */
		private int fence = tail;

		public boolean hasNext() {
			return cursor != fence;
		}

		public E next() {
			if (cursor == fence)
				throw new NoSuchElementException();
			@SuppressWarnings("unchecked")
			E result = (E) lements[cursor];
			// should not happen
			if (tail != fence || result == null)
				throw new ConcurrentModificationException();
			cursor = (cursor + 1) & (elements.length - 1);
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
			Object[] a = lements;
			int m = a.length - 1, f = fence, i = cursor;
			cursor = f;
			while (i != f) {
				@SuppressWarnings("unchecked")
				E e = (E) a[i];
				i = (i + 1) & m;
				if (e == null)
					throw new ConcurrentModificationException();
				action.accept(e);
			}
		}
	}

	private class UnmodifiableDecreasingIterator implements Iterator<E> {

		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		private int cursor = tail;

		private Object[] lements = new Object[elements.length];

		public UnmodifiableDecreasingIterator() {
			System.arraycopy(elements, 0, lements, 0, elements.length);
		}

		/**
		 * Tail recorded at construction (also in remove), to stop iterator and
		 * also to check for comodification.
		 */
		private int fence = head;

		public boolean hasNext() {
			return cursor != fence;
		}

		public E next() {
			if (cursor == fence)
				throw new NoSuchElementException();
			cursor = (cursor - 1) & (lements.length - 1);
			@SuppressWarnings("unchecked")
			E result = (E) lements[cursor];
			if (head != fence || result == null)
				throw new ConcurrentModificationException();
			return result;

		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final class UnmodifiableDeqSpliterator<E> implements Spliterator<E> {
		private final ImmutableDeque<E> deq;
		private int fence; // -1 until first use
		private int index; // current index, modified on traverse/split

		/** Creates new spliterator covering the given array and range */
		UnmodifiableDeqSpliterator(ImmutableDeque<E> deq, int origin, int fence) {
			this.deq = deq;
			this.index = origin;
			this.fence = fence;
		}

		private int getFence() { // force initialization
			int t;
			if ((t = fence) < 0) {
				t = fence = deq.tail;
				index = deq.head;
			}
			return t;
		}

		public UnmodifiableDeqSpliterator<E> trySplit() {
			int t = getFence(), h = index, n = deq.elements.length;
			if (h != t && ((h + 1) & (n - 1)) != t) {
				if (h > t)
					t += n;
				int m = ((h + t) >>> 1) & (n - 1);
				return new UnmodifiableDeqSpliterator<>(deq, h, index = m);
			}
			return null;
		}

		public void forEachRemaining(Consumer<? super E> consumer) {
			if (consumer == null)
				throw new NullPointerException();
			Object[] a = new Object[deq.elements.length];
			System.arraycopy(deq.elements, 0, a, 0, deq.elements.length);
			int m = a.length - 1, f = getFence(), i = index;
			index = f;
			while (i != f) {
				@SuppressWarnings("unchecked")
				E e = (E) a[i];
				i = (i + 1) & m;
				if (e == null)
					throw new ConcurrentModificationException();
				consumer.accept(e);
			}
		}

		public boolean tryAdvance(Consumer<? super E> consumer) {
			if (consumer == null)
				throw new NullPointerException();
			Object[] a = new Object[deq.elements.length];
			System.arraycopy(deq.elements, 0, a, 0, deq.elements.length);
			int m = a.length - 1, f = getFence(), i = index;
			if (i != f) {
				@SuppressWarnings("unchecked")
				E e = (E) a[i];
				index = (i + 1) & m;
				if (e == null)
					throw new ConcurrentModificationException();
				consumer.accept(e);
				return true;
			}
			return false;
		}

		public long estimateSize() {
			int n = getFence() - index;
			if (n < 0)
				n += deq.elements.length;
			return (long) n;
		}

		@Override
		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.SUBSIZED;
		}
	}

}
