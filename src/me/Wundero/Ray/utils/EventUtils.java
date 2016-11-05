package me.Wundero.Ray.utils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.event.Event;

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

public class EventUtils {

	/**
	 * Return a map that can be used as initial variable values for an event.
	 * Finds methods that are getters in the event class and executes them in
	 * order to retrieve up to date values. It also holds the values given by
	 * the cause, so long as they were not overwritten.
	 */
	public static Map<String, Object> getValues(Event e, boolean skipCause) {
		Map<String, Object> causeValues = skipCause ? Utils.sm() : e.getCause().getNamedCauses();
		causeValues.putAll(execMethods(e.getClass(), e));
		return causeValues;
	}

	@SuppressWarnings("unchecked")
	private static <T> Map<String, Object> execMethods(Class<? extends T> clazz, T obj) {
		Map<String, Object> out = Utils.sm();
		for (Method m : Utils.combine(clazz.getDeclaredMethods(), clazz.getMethods())) {
			if (canUseMethod(m)) {
				Optional<Object> ret = Optional.empty();
				try {
					ret = Utils.wrap(m.invoke(obj));
				} catch (Exception e) {
					ret = Optional.empty();
				}
				if (ret.isPresent()) {
					Object o = ret.get();
					if (o instanceof Optional) {
						((Optional<Object>) o).ifPresent(obj2 -> {
							out.put(sanitize(m.getName()), obj2);
						});
					} else {
						out.put(sanitize(m.getName()), o);
					}
				}
			}
		}
		return out;
	}

	private static boolean canUseMethod(Method m) {
		if (m.getReturnType().equals(Void.TYPE)) {
			return false;
		}
		if (m.getParameters().length > 0) {
			return false;
		}
		if (!m.isAccessible()) {
			return false;
		}
		return true;
	}

	private static String sanitize(String methodHeader) {
		methodHeader = methodHeader.toLowerCase().trim();
		if (methodHeader.startsWith("is")) {
			return methodHeader.substring(2);
		}
		if (methodHeader.startsWith("get")) {
			return methodHeader.substring(3);
		}
		return methodHeader;
	}

}
