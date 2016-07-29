package me.Wundero.ProjectRay.framework.mail;
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
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class Mail {
	private UUID uuid = UUID.randomUUID();
	private UUID from, to;
	private String message;
	private boolean read = false;
	private ConfigurationNode node;

	// generated node
	public Mail(String message, UUID from, UUID to, ConfigurationNode node) {
		this.setFrom(from);
		this.setTo(to);
		this.setMessage(message);
		this.node = node;
	}

	// loaded node
	public Mail(ConfigurationNode node) throws ObjectMappingException {
		this.node = node;
		setUuid(node.getNode("uuid").getValue(TypeToken.of(UUID.class)));
		setFrom(node.getNode("from").getValue(TypeToken.of(UUID.class)));
		setTo(node.getNode("to").getValue(TypeToken.of(UUID.class)));
		setRead(node.getNode("read").getBoolean(false));
		setMessage(node.getNode("message").getString());
	}

	@Override
	public String toString() {
		Map<String, String> v = Utils.sm();
		v.put("uuid", uuid.toString());
		v.put("from", from.toString());
		v.put("to", to.toString());
		v.put("message", message);
		v.put("read", String.valueOf(read));
		return v.toString();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Mail && ((Mail) o).uuid.equals(this.uuid);
	}

	public static TypeSerializer<Mail> serializer() {
		return new TypeSerializer<Mail>() {

			@Override
			public Mail deserialize(TypeToken<?> arg0, ConfigurationNode arg1) throws ObjectMappingException {
				return new Mail(arg1);
			}

			@Override
			public void serialize(TypeToken<?> arg0, Mail arg1, ConfigurationNode arg2) throws ObjectMappingException {
				arg1.node = arg2;
				arg1.save();
			}

		};
	}

	public void delete() {
		node.getParent().removeChild(node.getKey());
	}

	public void save() throws ObjectMappingException {
		node.getNode("uuid").setValue(TypeToken.of(UUID.class), uuid);
		node.getNode("to").setValue(TypeToken.of(UUID.class), to);
		node.getNode("from").setValue(TypeToken.of(UUID.class), from);
		node.getNode("message").setValue(message);
		node.getNode("read").setValue(read);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getFrom() {
		return from;
	}

	public void setFrom(UUID from) {
		this.from = from;
	}

	public UUID getTo() {
		return to;
	}

	public void setTo(UUID to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
}
