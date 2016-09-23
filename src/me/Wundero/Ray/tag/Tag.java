package me.Wundero.Ray.tag;

import java.util.Optional;

import org.spongepowered.api.text.Text;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.variables.ParsableData;

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

public abstract class Tag<T> {
	private final String name;
	protected T object;

	public Tag(String name, T object) {
		this.setObject(object);
		this.name = name;
		Ray.get().getTags().register(this);
	}
	
	public abstract boolean verify(Object o);

	public abstract Optional<Text> get(Optional<ParsableData> data);

	public final String getName() {
		return name;
	}

	public final T getObject() {
		return object;
	}

	public final void setObject(T object) {
		this.object = object;
	}

}
