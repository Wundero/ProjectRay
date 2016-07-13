package me.Wundero.ProjectRay.framework;

import java.util.Map;

import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.utils.Utils;
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
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Section extends DataHolder {

	protected ConfigurationNode section = null;

	public Section(ConfigurationNode node) {
		loadValues(node);
	}

	public final void loadValues(ConfigurationNode section) {
		this.putAll(getMap(section.getChildrenMap()));
		this.setSection(section);
		try {
			load(section);
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	private static Map<String, Object> getMap(Map<Object, ? extends ConfigurationNode> map) {
		Map<String, Object> out = Maps.newHashMap();
		for (Object o : map.keySet()) {
			out.put(o.toString(), map.get(o).getValue());
		}
		return out;
	}

	public abstract void load(ConfigurationNode sect) throws Exception;

	public ConfigurationNode getSection() {
		return section;
	}

	private void setSection(ConfigurationNode section) {
		this.section = section;
	}
}
