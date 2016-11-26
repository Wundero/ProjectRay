package me.Wundero.Ray.tag;

import java.util.Optional;

import org.spongepowered.api.text.Text;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.config.RaySerializable;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

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

/**
 * Represents a dynamic clickable object.
 */
public abstract class Tag<T> implements RaySerializable {
	private final String name;
	private String permissionView, permissionUse;
	protected T object;

	public Tag(String name, T object) {
		this.setObject(object);
		this.name = name.toLowerCase();
		Ray.get().getTags().register(this);
	}

	public Tag(ConfigurationNode node) {
		this.name = node.getKey().toString().toLowerCase();
		load(node);
	}

	public void save(ConfigurationNode node) {
		node = node.getNode(name);
		try {
			deserialize(node.getNode("tag"));
		} catch (Exception e) {
		}
		this.permissionUse = node.getNode("permission-use").getString();
		this.permissionView = node.getNode("permission-view").getString();
	}

	public void load(ConfigurationNode node) {
		node = node.getNode(name);
		try {
			serialize(node.getNode("tag"));
		} catch (Exception e) {
		}
		node.getNode("permission-use").setValue(permissionUse);
		node.getNode("permission-view").setValue(permissionView);
	}

	/**
	 * Verify that an object matches the generic type
	 */
	public abstract boolean verify(Object o);

	/**
	 * Get the textual representation of this tag as parsed with respect to the
	 * data.
	 */
	public abstract Optional<Text> get(Optional<ParsableData> data);

	/**
	 * Whether the data has permission to use this tag.
	 */
	public boolean hasPermission(ParsableData d) {
		boolean s = (permissionUse == null || permissionUse.isEmpty())
				|| d.getSender().isPresent() && d.getSender().get().hasPermission(permissionUse);
		boolean r = (permissionView == null || permissionView.isEmpty())
				|| d.getRecipient().isPresent() && d.getRecipient().get().hasPermission(permissionView);
		return s && r;
	}

	/**
	 * Get the name of this tag.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get the internal object this tag represents.
	 */
	public final T getObject() {
		return object;
	}

	/**
	 * Set the object for this tag.
	 */
	public final void setObject(T object) {
		this.object = object;
	}

	/**
	 * Get the permission required to view this tag
	 */
	public String getPermissionView() {
		return permissionView;
	}

	/**
	 * Set the permission required to view this tag
	 */
	public Tag<T> setPermissionView(String permissionView) {
		this.permissionView = permissionView;
		return this;
	}

	/**
	 * Get the permission required to use this tag
	 */
	public String getPermissionUse() {
		return permissionUse;
	}

	/**
	 * Set the permission required to use this tag
	 */
	public Tag<T> setPermissionUse(String permissionUse) {
		this.permissionUse = permissionUse;
		return this;
	}

}
