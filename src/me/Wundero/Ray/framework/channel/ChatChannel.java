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

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ChatChannel implements MutableMessageChannel, Comparable<ChatChannel> {

	private ChannelMemberCollection members = new ChannelMemberCollection();
	private String name;
	private String permission;
	private Text tag;
	private double range;
	private boolean hidden = false;
	private boolean autojoin = true;
	private boolean obfuscateRanged = false;
	private ConfigurationNode node;

	public static TypeSerializer<ChatChannel> serializer() {
		return new TypeSerializer<ChatChannel>() {

			@Override
			public ChatChannel deserialize(TypeToken<?> arg0, ConfigurationNode arg1) throws ObjectMappingException {
				ChatChannel out = new ChatChannel();
				out.name = arg1.getNode("name").getString();
				out.permission = arg1.getNode("permission").getString(null);
				out.tag = TextSerializers.FORMATTING_CODE
						.deserialize(arg1.getNode("tag").getString("[" + out.name.charAt(0) + "]"));
				out.members = arg1.getNode("members").getValue(TypeToken.of(ChannelMemberCollection.class));
				out.range = arg1.getNode("range").getDouble(-1);
				out.hidden = arg1.getNode("hidden").getBoolean(false);
				out.autojoin = arg1.getNode("autojoin").getBoolean(true);
				out.obfuscateRanged = arg1.getNode("range-obfuscation").getBoolean(false);
				out.node = arg1;
				return out;
			}

			@Override
			public void serialize(TypeToken<?> arg0, ChatChannel arg1, ConfigurationNode arg2)
					throws ObjectMappingException {
				arg2.getNode("name").setValue(arg1.name);
				if (arg1.permission != null && !arg1.permission.isEmpty()) {
					arg2.getNode("permission").setValue(arg1.permission);
				}
				arg2.getNode("tag").setValue(TextSerializers.FORMATTING_CODE.serialize(arg1.tag));
				arg2.getNode("members").setValue(TypeToken.of(ChannelMemberCollection.class), arg1.members);
				arg2.getNode("range").setValue(arg1.range);
				arg2.getNode("hidden").setValue(arg1.hidden);
				arg2.getNode("autojoin").setValue(arg1.autojoin);
				arg2.getNode("range-obfuscation").setValue(arg1.obfuscateRanged);
			}
		};
	}

	public ChatChannel() {
		members.addAll(MessageChannel.TO_CONSOLE.getMembers());
	}

	private boolean c(MessageReceiver recipient) {
		if (recipient instanceof Player) {
			return members.contains(((Player) recipient).getUniqueId());
		}
		return members.contains(recipient);
	}

	@Override
	public Optional<Text> transformMessage(Object sender, MessageReceiver recipient, Text original, ChatType type) {
		if (!c(recipient) || members.get(recipient).isBanned()) {
			return Optional.empty();
		}
		if (sender instanceof MessageReceiver
				&& (!members.contains((MessageReceiver) sender) || !members.get((MessageReceiver) sender).canSpeak())) {
			return Optional.empty();
		}
		if (range > 0 && range < Double.MAX_VALUE && sender instanceof Player && recipient instanceof Player) {
			Player p = (Player) sender;
			Player r = (Player) recipient;
			boolean ir = Utils.inRange(p.getLocation(), r.getLocation(), range);
			double delta = Utils.difference(p.getLocation(), r.getLocation());
			if (!ir && !this.isObfuscateRanged()) {
				return Optional.empty();
			} else if (!ir) {
				double percentObfuscation = ((delta - range) / (delta * (2 * (range / delta)))) * 100;
				double percentDiscoloration = ((delta - range) / delta) * 100;
				return Optional.of(TextUtils.obfuscate(original, percentObfuscation, percentDiscoloration));
			}
		}
		return Optional.of(original);
	}

	public double range() {
		return range;
	}

	public boolean canJoin(Player player) {
		if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
			return false;
		}
		if (members == null) {
			members = new ChannelMemberCollection();
			members.addAll(MessageChannel.TO_CONSOLE.getMembers());
		}
		ChannelMember m = members.get(player);
		if (m != null && m.isBanned()) {
			return false;
		}
		return true;
	}

	@Override
	public Collection<MessageReceiver> getMembers() {
		return members.getMembers();
	}

	@Override
	public boolean addMember(MessageReceiver member) {
		if (c(member)) {
			if (member instanceof Player) {
				members.remove(((Player) member).getUniqueId());
			} else {
				members.remove(member);
			}
		}
		if (member instanceof Player) {
			Player p = (Player) member;
			if (permission != null && !permission.isEmpty() && !p.hasPermission(permission)) {
				return false;
			}
		}
		return members.add(member);
	}

	@Override
	public boolean removeMember(MessageReceiver member) {
		return members.remove(member);
	}

	public boolean removeMember(UUID uuid) {
		return members.remove(uuid);
	}

	public void banMember(MessageReceiver member) {
		members.get(member).setBanned(true);
	}

	public boolean setMemberRole(MessageReceiver member, Role role) {
		if (!members.contains(member)) {
			return false;
		}
		members.get(member).setRole(role);
		return true;
	}

	public boolean muteMember(MessageReceiver member) {
		boolean r = !members.get(member).isMuted();
		members.get(member).setMuted(true);
		return r;
	}

	public boolean muteMember(MessageReceiver member, long time, TimeUnit unit) {
		if (muteMember(member)) {
			Ray.get().registerTask(Task.builder().async().delay(time, unit).execute(() -> {
				unmuteMember(member);
			}).submit(Ray.get().getPlugin()));
		}
		return false;
	}

	public boolean unmuteMember(MessageReceiver member) {
		boolean r = members.get(member).isMuted();
		members.get(member).setMuted(false);
		return r;
	}

	@Override
	public void clearMembers() {
		members.clear();
	}

	public Text getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	public String getPermission() {
		return permission;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public ConfigurationNode getNode() {
		return node;
	}

	public boolean isAutojoin() {
		return autojoin;
	}

	public void setAutojoin(boolean autojoin) {
		this.autojoin = autojoin;
	}

	public ChannelMemberCollection getMembersCollection() {
		return members;
	}

	@Override
	public int compareTo(ChatChannel o) {
		int i = members.size() - o.members.size();
		if (i == 0) {
			if (range <= 0) {
				if (o.range > 0) {
					return (int) o.range;
				} else {
					return 0;
				}
			} else {
				if (o.range <= 0) {
					return (int) -range;
				} else {
					return (int) (range - o.range);
				}
			}
		}
		return i;
	}

	/**
	 * @return the obfuscateRanged
	 */
	public boolean isObfuscateRanged() {
		return obfuscateRanged;
	}

	/**
	 * @param obfuscateRanged
	 *            the obfuscateRanged to set
	 */
	public void setObfuscateRanged(boolean obfuscateRanged) {
		this.obfuscateRanged = obfuscateRanged;
	}

}
