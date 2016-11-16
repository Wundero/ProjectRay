package me.Wundero.Ray.variables;
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

import java.util.Map;
import java.util.Optional;

import me.Wundero.Ray.utils.Utils;

/**
 * Object that stores all loaded variables and wrappers.
 */
public class Store {
	private Map<String, Variable> vars = Utils.sm();
	private Map<String, VariableWrapper> wrappers = Utils.sm();

	/**
	 * Get a wrapper if available
	 */
	public Optional<VariableWrapper> getWrapper(String key) {
		if (!wrappers.containsKey(key)) {
			return Optional.empty();
		}
		return Optional.ofNullable(wrappers.get(key));
	}

	/**
	 * Add a new wrapper
	 */
	public boolean registerWrapper(VariableWrapper v) {
		if (wrappers.containsKey(v.getKey())) {
			return false;
		}
		wrappers.put(v.getKey(), v);
		return true;
	}

	/**
	 * Get a variable if available
	 */
	public Optional<Variable> getVariable(String key) {
		if (!vars.containsKey(key)) {
			return Optional.empty();
		}
		return Optional.ofNullable(vars.get(key));
	}

	/**
	 * Add a new variable
	 */
	public boolean registerVariable(Variable v) {
		if (vars.containsKey(v.getKey())) {
			return false;
		}
		vars.put(v.getKey(), v);
		return true;
	}
}
