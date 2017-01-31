package me.Wundero.Ray.framework.trigger;
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

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.variables.ParsableData;

public class FormatEvent extends AbstractEvent implements Cancellable {

	private boolean c;
	private final Cause cause;
	private Format format;
	private ParsableData data;
	
	public FormatEvent(Cause cause, Format format, ParsableData data) {
		this.cause = cause;
		this.format = format;
		this.data = data;
	}

	public ParsableData getData() {
		return data;
	}

	public void setData(ParsableData data) {
		this.data = data;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.c = cancel;
	}

	@Override
	public Cause getCause() {
		return this.cause;
	}

}
