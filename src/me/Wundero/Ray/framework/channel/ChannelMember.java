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

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * A class representing a member in a chat channel.
 */
public class ChannelMember {
	private final MessageReceiver receiver;
	private UUID uuid = UUID.randomUUID();
	private boolean muted;
	private Role role;
	private boolean banned;

	/**
	 * @return internal UUID.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @return internal UUID.
	 */
	public UUID getUniqueId() {
		return getUUID();
	}

	/**
	 * Set the interna uuid.
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		String out = "uuid=" + uuid.toString();
		out += ", object=" + receiver.toString();
		out += ", banned=" + banned;
		out += ", muted=" + muted;
		out += ", role=" + role.name();
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (uuid == null) {
			return o == null;
		}
		if (o instanceof UUID) {
			return uuid.equals(o);
		}
		if (o instanceof Player) {
			return ((Player) o).getUniqueId().equals(uuid);
		}
		if (o instanceof ChannelMember) {
			return uuid.equals(((ChannelMember) o).uuid);
		}
		if (this.receiver == null) {
			return false;
		}
		if (o instanceof MessageReceiver) {
			return receiver.equals(o);
		}
		return false;
	}

	/**
	 * Return the type serializer for this class that converts this into a
	 * config file.
	 */
	public static TypeSerializer<ChannelMember> serializer() {
		return new TypeSerializer<ChannelMember>() {

			@Override
			public ChannelMember deserialize(TypeToken<?> arg0, ConfigurationNode arg1) throws ObjectMappingException {
				UUID uuid = arg1.getNode("uuid").getValue(TypeToken.of(UUID.class));
				boolean mute = arg1.getNode("muted").getBoolean(false);
				boolean banned = arg1.getNode("banned").getBoolean(false);
				Role role = Role.valueOf(arg1.getNode("role").getString());
				Optional<Player> player = Sponge.getServer().getPlayer(uuid);
				ChannelMember member = new ChannelMember(null);
				if (player.isPresent()) {
					member = new ChannelMember(player.get());
				}
				member.uuid = uuid;
				member.muted = mute;
				member.banned = banned;
				member.role = role;
				return member;
			}

			@Override
			public void serialize(TypeToken<?> arg0, ChannelMember arg1, ConfigurationNode arg2)
					throws ObjectMappingException {
				arg2.getNode("uuid").setValue(TypeToken.of(UUID.class), arg1.uuid);
				arg2.getNode("muted").setValue(arg1.muted);
				arg2.getNode("banned").setValue(arg1.banned);
				arg2.getNode("role").setValue(arg1.role.toString());
			}

		};
	}

	/**
	 * Wrap a message receiver in channel member.
	 */
	public ChannelMember(MessageReceiver r, Role role) {
		this.receiver = r;
		if (r instanceof Player) {
			this.uuid = ((Player) r).getUniqueId();
		}
		this.setRole(role);
		this.setBanned(false);
		this.setMuted(false);
	}

	/**
	 * Wrap a message receiver in channel member.
	 */
	public ChannelMember(MessageReceiver r) {
		this(r, Role.MEMBER);
	}

	/**
	 * Return the receiver wrapped by this member.
	 */
	public MessageReceiver getReceiver() {
		return receiver;
	}

	/**
	 * @return whether the user can speak.
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * Set whether the user can speak.
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	/**
	 * Return the permissibility of the user.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Set the permissibility of the user.
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * @return whether the user is banned from the channel.
	 */
	public boolean isBanned() {
		return banned;
	}

	/**
	 * Set whether the user is banned from the channel.
	 */
	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	/**
	 * @return whether the user is allowed to speak.
	 */
	public boolean canSpeak() {
		return role != Role.GUEST && !muted && !banned;
	}
}
