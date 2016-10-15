package me.Wundero.Ray.framework.channel;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Wrapper class representing a list of members. Has a few additional changes
 * over list.
 */
public class ChannelMemberCollection {
	private List<ChannelMember> members = Utils.sl();

	/**
	 * Remove duplicates.
	 */
	public void fix() {
		members = Utils.removeDuplicates(members);
		List<ChannelMember> m = Utils.sl();
		for (ChannelMember me : members) {
			if (!m.contains(me)) {
				m.add(me);
			}
		}
		if (m.size() < members.size()) {
			members = m;
		}
	}

	/**
	 * Return the list of message receivers.
	 */
	public List<MessageReceiver> getMembers() {
		fix();
		List<MessageReceiver> l = Utils.sl();
		for (ChannelMember m : members) {
			l.add(m.getReceiver());
		}
		return Collections.unmodifiableList(l);
	}

	/**
	 * Serialize a list of members.
	 */
	public static TypeSerializer<ChannelMemberCollection> serializer() {
		return new TypeSerializer<ChannelMemberCollection>() {

			@Override
			public ChannelMemberCollection deserialize(TypeToken<?> arg0, ConfigurationNode arg1)
					throws ObjectMappingException {
				ChannelMemberCollection out = new ChannelMemberCollection();
				for (ConfigurationNode node : arg1.getChildrenMap().values()) {
					out.add(node.getValue(TypeToken.of(ChannelMember.class)));
				}
				return out;
			}

			@Override
			public void serialize(TypeToken<?> arg0, ChannelMemberCollection arg1, ConfigurationNode arg2)
					throws ObjectMappingException {
				for (ChannelMember m : arg1.members) {
					ConfigurationNode mn = arg2.getNode(m.getUUID().toString());
					mn.setValue(TypeToken.of(ChannelMember.class), m);
				}
			}
		};
	}

	@Override
	public String toString() {
		fix();
		return members.toString();
	}

	/**
	 * Return the size of the list.
	 */
	public int size() {
		fix();
		return members.size();
	}

	/**
	 * Add all members to the list.
	 */
	public void addAll(Iterable<MessageReceiver> receivers) {
		for (MessageReceiver r : receivers) {
			add(r);
		}
	}

	/**
	 * Clear the lsit.
	 */
	public void clear() {
		members.clear();
	}

	/**
	 * Return the member wrapper for the receiver.
	 */
	public ChannelMember get(MessageReceiver receiver) {
		if (!contains(receiver)) {
			return null;
		}
		return members.get(members.indexOf(receiver));
	}

	/**
	 * Return the member wrapper for the UUID.
	 */
	public ChannelMember get(UUID receiver) {
		if (!contains(receiver)) {
			return null;
		}
		return members.get(members.indexOf(receiver));
	}

	/**
	 * @return whether the list contains the receiver.
	 */
	public boolean contains(MessageReceiver receiver) {
		return members.contains(receiver);
	}

	/**
	 * @return whether the list contains the member.
	 */
	public boolean contains(ChannelMember member) {
		return members.contains(member);
	}

	/**
	 * @return whether the list contains the uuid.
	 */
	public boolean contains(UUID uuid) {
		return members.contains(uuid);
	}

	/**
	 * Remove the member.
	 */
	public boolean remove(UUID uuid) {
		return members.remove(uuid);
	}

	/**
	 * Remove the member.
	 */
	public boolean remove(ChannelMember member) {
		return members.remove(member);
	}

	/**
	 * Remove the member.
	 */
	public boolean remove(MessageReceiver member) {
		return members.remove(member);
	}

	/**
	 * Add the receiver; the receiver is automatically wrapped.
	 */
	public boolean add(MessageReceiver r) {
		return add(new ChannelMember(r));
	}

	/**
	 * Add the member.
	 */
	public boolean add(ChannelMember member) {
		if (members.contains(member)) {
			return false;
		}
		members.add(member);
		fix();
		return true;
	}
}
