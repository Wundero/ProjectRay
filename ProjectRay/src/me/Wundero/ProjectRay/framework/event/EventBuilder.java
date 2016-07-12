package me.Wundero.ProjectRay.framework.event;
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

import java.util.List;
import java.util.concurrent.Callable;

import org.spongepowered.api.event.cause.Cause;

import me.Wundero.ProjectRay.framework.iface.Sendable;

public class EventBuilder {
	private Cause cause;
	private String name;
	private Callable<List<Sendable>> objmaker;

	public JsonEvent build() {
		if (name == null || cause == null) {
			return null;
		}
		JsonEvent event = new JsonEvent(name, cause) {

			@Override
			public void createObjects() {
				if (objmaker == null) {
					return;
				}
				try {
					this.objects = objmaker.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return event;
	}

	private EventBuilder() {
	}

	public EventBuilder withObjectCreator(Callable<List<Sendable>> creator) {
		objmaker = creator;
		return this;
	}

	public static EventBuilder builder() {
		return new EventBuilder();
	}

	public EventBuilder withCause(Cause c) {
		cause = c;
		return this;
	}

	public EventBuilder withName(String name) {
		this.name = name;
		return this;
	}
}
