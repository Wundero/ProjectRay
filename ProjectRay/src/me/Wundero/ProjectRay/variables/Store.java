package me.Wundero.ProjectRay.variables;

import java.util.HashMap;

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
public class Store {

	private HashMap<String, Variable> vars = new HashMap<>();

	private static Store store;

	private Store() {
		vars = new HashMap<>();
	}

	public void clear() {
		for (String s : vars.keySet()) {
			remove(s);
		}
		vars.clear();
	}

	public void clear(boolean unsafe) {
		if (!unsafe) {
			clear();
		} else {
			vars.clear();
		}
	}

	public boolean has(String id) {
		return get(id) != null;
	}

	public static Store get() {
		if (store == null) {
			store = new Store();
		}
		return store;
	}

	public Variable get(String id) {
		return vars.get(Parser.get().fix(id.toLowerCase()));
	}

	public boolean remove(Variable v) {
		return remove(v.getName());
	}

	public boolean remove(String id) {
		if (!has(id)) {
			return false;
		}
		this.vars.remove(Parser.get().fix(id.toLowerCase())).unregister();
		return true;
	}

	public boolean add(Variable v) {
		if (v == null) {
			return false;
		}
		if (v.getName() == null) {
			return false;
		}
		String id = v.getName();
		id = id.toLowerCase();
		vars.put(id, v);
		v.register();
		return true;
	}
}
