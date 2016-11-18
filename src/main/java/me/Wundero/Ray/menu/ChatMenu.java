package me.Wundero.Ray.menu;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.utils.RayCollectors;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;

public class ChatMenu extends Menu {

	private static class TextHolder implements Comparable<TextHolder> {
		private Text text;
		private UUID uuid;
		private int id;

		public TextHolder(Text message, UUID uuid, int id) {
			this.text = message;
			this.uuid = uuid;
			this.id = id;
		}

		public Text getText() {
			return text;
		}

		public int id() {
			return id;
		}

		public TextHolder id(int i) {
			this.id = i;
			return this;
		}

		public UUID getUUID() {
			return uuid;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof UUID ? uuid.equals(o)
					: (o instanceof TextHolder ? ((TextHolder) o).uuid.equals(uuid) : false);
		}

		@Override
		public int compareTo(TextHolder o) {
			return o.id - id;
		}
	}

	public void deactive() {
		this.active = false;
	}

	public void activate() {
		this.active = true;
		this.unread = 0;
	}

	// NOTE: UNORDERED LIST
	private List<TextHolder> messages = Utils.sl();
	private Map<UUID, Text> replaced = Utils.sm();
	private boolean active = false;
	private ChatMenu toUpdate = null;
	private ChatChannel srcChannel = null;
	private int unread = 0;

	public ChatChannel getChannel() {
		return srcChannel;
	}

	public ChatMenu(Player player, Menu from, String name, ChatChannel src) {
		super(player, from);
		this.fillSpacesFromTop = true;
		this.scrolling = true;
		this.title = createTitle(name);
		this.srcChannel = src;
	}

	public ChatMenu(Player player, String name, ChatChannel src) {
		super(player);
		this.srcChannel = src;
		this.fillSpacesFromTop = true;
		this.scrolling = true;
		this.title = createTitle(name);
	}

	protected void modIndices(int by) {
		synchronized (messages) {
			messages = messages.stream().map(holder -> {
				holder = holder.id(holder.id + by);
				return holder;
			}).collect(RayCollectors.syncList());
		}
	}

	protected void incIndices() {
		modIndices(1);
	}

	protected void decIndices() {
		modIndices(-1);
	}

	protected Text restore(UUID original) {
		return replace(original, replaced.get(original));
	}

	protected Text replace(UUID original, Text with) {
		int index = messages.indexOf(original);
		Text out = messages.get(index).text;
		TextHolder h = messages.get(index);
		h.text = with;
		synchronized (messages) {
			messages.set(index, h);
		}
		this.replaced.put(original, out);
		sendOrUnread();
		return out;
	}

	protected TextHolder remove(UUID u) {
		if (!getPlayer().isPresent()) {
			return null;
		}
		TextHolder h = messages.get(messages.indexOf(u));
		if (getPlayer().get().hasPermission("ray.removemessage")) {
			replace(u, Text.of(TextColors.RED, restoreAction(u), restoreHover(), "Message has been removed."));
			return h;
		}
		synchronized (messages) {
			final int id = h.id();
			messages = messages.stream().filter(holder -> !holder.getUUID().equals(u)).map(holder -> {
				if (holder.id > id) {
					holder = holder.id(holder.id - 1);
					return holder;
				} else {
					return holder;
				}
			}).collect(RayCollectors.syncList());
			addSpace();
		}
		return h;
	}

	public List<Text> getSorted() {
		List<TextHolder> other = Utils.al();
		synchronized (messages) {
			other = Utils.al(messages, true);
		}
		List<Text> out = other.stream().sorted(new Comparator<TextHolder>() {
			@Override
			public int compare(TextHolder o1, TextHolder o2) {
				return o1.compareTo(o2);
			}
		}).map(holder -> {
			return holder.getText();
		}).collect(RayCollectors.rayList());
		return out;
	}

	protected void addSpace() {
		synchronized (messages) {
			messages.add(new TextHolder(TextUtils.SPACE(), UUID.randomUUID(), 99));
		}
	}

	protected ClickAction<?> restoreAction(UUID u) {
		return TextActions.executeCallback(source -> {
			if (source.hasPermission("ray.removemessage")) {
				restore(u);
				sendOrUnread();
			} else {
				source.sendMessage(Text.of(TextColors.RED, "You are not allowed to do that!"));
			}
		});
	}

	protected HoverAction<?> restoreHover() {
		return TextActions.showText(Text.of(TextColors.AQUA, "Click to restore this message!"));
	}

	protected ClickAction<?> removeAction(UUID u) {
		return TextActions.executeCallback(source -> {
			if (source.hasPermission("ray.removemessage")) {
				remove(u);
				sendOrUnread();
			} else {
				source.sendMessage(Text.of(TextColors.RED, "You are not allowed to do that!"));
			}
		});
	}

	protected HoverAction<?> removeHover() {
		return TextActions.showText(Text.of(TextColors.AQUA, "Click to remove this message!"));
	}

	protected List<ChatMenu> getChatMenus() {
		if (!getPlayer().isPresent()) {
			throw new IllegalArgumentException("Player must be online!");
		}
		List<ChatMenu> out = Utils.al();
		List<String> names = Utils.al();
		for (String listening : RayPlayer.get(holderUUID).getListenChannels()) {
			ChatChannel ch = Ray.get().getChannels().getChannel(listening);
			if (ch == null || !ch.getMenus().containsKey(holderUUID)) {
				continue;
			}
			ChatMenu men = ch.getMenus().get(holderUUID);
			if (men != null && men != this && !names.contains(men.getChannel().getName())) {
				out.add(men);
				names.add(men.getChannel().getName());
			}
		}
		return out;
	}

	protected void incUnread() {
		unread++;
		if (toUpdate != null) {
			toUpdate.sendOrUnread();
		}
	}

	protected Text removeButton(UUID u) {
		return Text.of(TextColors.RED, removeAction(u), removeHover(), "[X]");
	}

	@SuppressWarnings("unchecked")
	protected Text nameButton(ChatMenu caller) {
		Text t = this.title;
		if (this.title.getClickAction().isPresent()) {
			ClickAction<?> c = this.title.getClickAction().get();
			if (c.getResult() instanceof Consumer) {
				t = t.toBuilder().onClick(TextActions.executeCallback(src -> {
					if (src instanceof Player) {
						RayPlayer.get((Player) src).setActiveChannel(getChannel());
					}
					((Consumer<CommandSource>) c.getResult()).accept(src);
				})).build();
			}
		}
		t = t.toBuilder().onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to switch channels!"))).build();
		this.toUpdate = caller;
		if (unread > 0) {
			t = t.concat(TextUtils.SPACE()).concat(Text.of(TextColors.RED, "(" + unread + ")"));
		}
		return t;
	}

	private void fixSize() {
		synchronized (messages) {
			messages = messages.stream().filter(holder -> holder.id < 100).collect(RayCollectors.syncList());
		}
	}

	public void addMessage(CommandSource sender, Text message, UUID uuid) {
		incIndices();
		if (sender.hasPermission("ray.removemessage.exempt")) {
			messages.add(0, new TextHolder(message, uuid, 0));
			fixSize();
		} else {
			Text rb = removeButton(uuid);
			Text text = rb.concat(TextUtils.SPACE()).concat(message);
			messages.add(0, new TextHolder(text, uuid, 0));
			fixSize();
		}
		sendOrUnread();
	}

	public boolean isActive() {
		return active;
	}

	protected void sendOrUnread() {
		if (active) {
			send();
		} else {
			incUnread();
		}
	}

	@Override
	public void send() {
		if (!active) {
			RayPlayer.get(holderUUID).getActiveMenu().active = false;
			active = true;
			RayPlayer.get(holderUUID).setActiveMenu(this);
			unread = 0;
		}
		super.send();
	}

	@Override
	public List<Text> renderBody() {
		return this.getSorted();
	}

	@Override
	public List<Text> renderHeader() {
		return Utils.al();
	}

	@Override
	public List<Text> renderFooter() {
		Text otherCMs = Text.of("").concat(nameButton(null));
		Text separator = Text.of(TextColors.GRAY, " | ");
		for (ChatMenu m : this.getChatMenus()) {
			otherCMs = otherCMs.concat(separator).concat(m.nameButton(this));
		}
		return Utils.al(otherCMs);
	}

}
