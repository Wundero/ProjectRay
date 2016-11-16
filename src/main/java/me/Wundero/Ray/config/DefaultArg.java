package me.Wundero.Ray.config;
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

import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.TextTemplate.Arg;

//really only useful for internals, but nice to have; saves time when repeating similar args
/**
 * Default argument for default formats to be loaded on setup into
 * configuration.
 */
public class DefaultArg {
	private Arg.Builder builder;
	private InternalClickAction<?> click;
	private InternalHoverAction<?> hover;
	private boolean optional = false;

	/**
	 * Create a new Default Arg
	 */
	public DefaultArg(Arg.Builder b, InternalClickAction<?> c, InternalHoverAction<?> h) {
		this.setBuilder(b);
		this.setClick(c);
		this.setHover(h);
	}

	/**
	 * Create a new DefaultArg
	 */
	public DefaultArg(String s, InternalClickAction<?> c, InternalHoverAction<?> h) {
		this(TextTemplate.arg(s), c, h);
	}

	/**
	 * Return the argument
	 */
	public Arg.Builder getBuilder() {
		return builder;
	}

	/**
	 * Set the argument
	 */
	public DefaultArg setBuilder(Arg.Builder builder) {
		this.builder = builder;
		return this;
	}

	/**
	 * Get the click action associated with this arg
	 */
	public InternalClickAction<?> getClick() {
		return click;
	}

	/**
	 * Set the click action associated with this arg.
	 */
	public DefaultArg setClick(InternalClickAction<?> click) {
		this.click = click;
		return this;
	}

	/**
	 * Get the hover action associated with this arg.
	 */
	public InternalHoverAction<?> getHover() {
		return hover;
	}

	/**
	 * Set the hover action associated with this arg.
	 */
	public DefaultArg setHover(InternalHoverAction<?> hover) {
		this.hover = hover;
		return this;
	}

	/**
	 * @return whether the arg is optional.
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Set whether the arg is optional.
	 */
	public DefaultArg setOptional(boolean optional) {
		this.optional = optional;
		return this;
	}

}
