package me.Wundero.ProjectRay.utils;

public interface Modifier<T, P> {

	public T modify(T object, @SuppressWarnings("unchecked") P... params);

}
