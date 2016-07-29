package me.Wundero.ProjectRay.framework.channel;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;

import com.google.common.base.Preconditions;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;

public class ChatChannel implements MutableMessageChannel {

	private List<MessageReceiver> members = Utils.sl();
	private String name;
	private String permission;
	private Text tag;
	private double range;
	private List<MessageReceiver> mutes = Utils.sl();
	private boolean hidden = false;
	private ConfigurationNode node;
	// TODO
	/*
	 * autojoin quickmessage multiworld enable/disable moderators bans whitelist
	 * etc.
	 */

	public ChatChannel(ConfigurationNode node) {
		this();
		this.setNode(Preconditions.checkNotNull(node));
		this.name = "";// TODO set this
		this.permission = "ray.channel." + name;
	}

	private ChatChannel() {
		members.addAll(MessageChannel.TO_CONSOLE.getMembers());
	}

	@Override
	public Optional<Text> transformMessage(Object sender, MessageReceiver recipient, Text original, ChatType type) {
		if (!members.contains(recipient)) {
			return Optional.empty();
		}
		if (range > 0 && sender instanceof Player && recipient instanceof Player) {
			Player p = (Player) sender;
			Player r = (Player) recipient;
			if (!Utils.inRange(p.getLocation(), r.getLocation(), range)) {
				return Optional.empty();
			}
		}
		return Optional.of(original);
	}

	@Override
	public Collection<MessageReceiver> getMembers() {
		return Collections.unmodifiableList(members);
	}

	@Override
	public boolean addMember(MessageReceiver member) {
		if (members.contains(member)) {
			return false;
		}
		if (member instanceof Player) {
			Player p = (Player) member;
			if (!p.hasPermission(permission)) {
				return false;
			}
		}
		return members.add(member);
	}

	@Override
	public boolean removeMember(MessageReceiver member) {
		return members.remove(member);
	}

	public boolean muteMember(MessageReceiver member) {
		if (mutes.contains(member)) {
			return false;
		}
		return mutes.add(member);
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
		return mutes.remove(member);
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

	private void setNode(ConfigurationNode node) {
		this.node = node;
	}
}
