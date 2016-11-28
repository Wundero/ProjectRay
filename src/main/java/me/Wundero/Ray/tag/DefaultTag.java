package me.Wundero.Ray.tag;
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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Pseudp-enum implementation for a tag that can be loaded
 */
public class DefaultTag {

	private Tag<?> tag;

	public DefaultTag() {
	}

	/**
	 * Add a tag
	 */
	public DefaultTag withTag(Tag<?> t) {
		this.tag = t;
		return this;
	}

	public Tag<?> getTag() {
		return tag;
	}

	/**
	 * Add a selectable tag
	 */
	public SelectableBuilder withSelectable(String name) {
		return new SelectableBuilder(this, name);
	}

	public static class SelectableBuilder {

		private DefaultTag tag;
		private String name;
		private Map<String, TextTemplate> map = Utils.hm();

		public SelectableBuilder(DefaultTag tag, String name) {
			this.tag = tag;
			this.name = name;
		}

		public DefaultTag build() {
			return tag.withTag(new SelectableTag(name, map));
		}

		public SelectableBuilder with(String key, TextTemplate template) {
			this.map.put(key, template);
			return this;
		}

		public SelectableBuilder with(String key, Text arg) {
			this.map.put(key, TextTemplate.of(arg));
			return this;
		}
	}

	/**
	 * Serialize into a config file
	 */
	public void applyTo(ConfigurationNode node) {
		try {
			tag.serialize(node);
		} catch (Exception e) {
		}
	}

}
