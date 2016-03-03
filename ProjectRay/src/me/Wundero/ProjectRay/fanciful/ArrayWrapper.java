package me.Wundero.ProjectRay.fanciful;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ArrayWrapper<E> {
	private E[] _array;

	public ArrayWrapper(E... paramVarArgs) {
		setArray(paramVarArgs);
	}

	public E[] getArray() {
		return this._array;
	}

	public void setArray(E[] paramArrayOfE) {
		Validate.notNull(paramArrayOfE, "The array must not be null.");
		this._array = paramArrayOfE;
	}

	public boolean equals(Object paramObject) {
		if (!(paramObject instanceof ArrayWrapper)) {
			return false;
		}
		return Arrays.equals(this._array, ((ArrayWrapper) paramObject)._array);
	}

	public int hashCode() {
		return Arrays.hashCode(this._array);
	}

	public static <T> T[] toArray(Iterable<? extends T> paramIterable,
			Class<T> paramClass) {
		int i = -1;
		Object localObject1;
		if ((paramIterable instanceof Collection)) {
			localObject1 = (Collection) paramIterable;
			i = ((Collection) localObject1).size();
		}
		if (i < 0) {
			i = 0;
			for (Iterator localIterator1 = paramIterable.iterator(); localIterator1
					.hasNext();) {
				localObject1 = (Object) localIterator1.next();
				i++;
			}
		}
		localObject1 = (Object[]) Array.newInstance(paramClass, i);
		int j = 0;
		for (Object localObject2 : paramIterable) {
			((Object[]) localObject1)[(j++)] = localObject2;
		}
		return (T[]) localObject1;
	}
}
