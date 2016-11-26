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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * Storage for all tags.
 */
public class TagStore {

	private Map<String, Tag<?>> tags = Utils.sm();
	private ConfigurationNode node;

	public void load(ConfigurationNode node) {
		this.node = node;
		ConfigurationNode propNode = node.getNode("tags");
		if (propNode.isVirtual()) {
			loadDefaults(node);
		} else {
			for (ConfigurationNode child : propNode.getChildrenMap().values()) {
				String clasNam = child.getNode("type").getString();
				try {
					Tag<?> t = (Tag<?>) Class.forName(clasNam).getConstructor(ConfigurationNode.class)
							.newInstance(child);
					register(t);
				} catch (Exception e) {
				}
			}
		}
	}

	public void loadDefaults(ConfigurationNode node) {
		DefaultTags.applyAll(node);
	}

	public void save() throws ObjectMappingException {
		ConfigurationNode propNode = node.getNode("tags");
		for (String s : tags.keySet()) {
			tags.get(s).save(propNode);
			propNode.getNode(s, "type").setValue(tags.get(s).getClass().getName());
		}
	}

	public void deregister(String name) {
		if (has(name)) {
			this.tags.remove(name);
		}
	}

	/**
	 * Register a new tag
	 */
	public void register(Tag<?> t) {
		if (has(t.getName())) {
			return;
		}
		this.tags.put(t.getName().toLowerCase(), t);
	}

	/**
	 * Check to see if a tag exists
	 */
	public boolean has(String name) {
		return tags.containsKey(name);
	}

	/**
	 * Get all tags of a type if they exist
	 */
	public <T, R extends Tag<T>> List<R> getAll(T verifiable, Class<R> tagClass) {
		List<R> out = Utils.al();
		for (Tag<?> t : tags.values()) {
			if (t == null) {
				continue;
			}
			if (tagClass.isAssignableFrom(t.getClass())) {
				if (!t.verify(verifiable)) {
					continue;
				}
				Utils.wrap(Utils.cast(t, tagClass)).ifPresent(s -> s.ifPresent(s2 -> out.add(s2)));
			}
		}
		return out;
	}

	/**
	 * Get a tag, if it exists
	 */
	@SuppressWarnings("unchecked")
	public <T, R extends Tag<T>> Optional<R> get(String name, T verifiable, Class<R> tagClass) {
		Tag<?> t = get(name).orElse(null);
		if (t == null) {
			return Optional.empty();
		}
		if (tagClass.isAssignableFrom(t.getClass())) {
			if (!t.verify(verifiable)) {
				return Optional.empty();
			}
			return Optional.ofNullable((R) t);
		}
		return Optional.empty();
	}

	/**
	 * Get a tag, if it exists
	 */
	public Optional<Tag<?>> get(String name) {
		Validate.notEmpty(name);
		return Optional.ofNullable(tags.get(name));
	}

}
