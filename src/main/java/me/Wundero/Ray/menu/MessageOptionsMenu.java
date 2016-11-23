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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.conversation.TextEditConversation;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.utils.Utils;

public class MessageOptionsMenu extends Menu {

	private Text message;
	private final Text originalMessage;
	private final WeakReference<Player> sender;
	private final UUID messageUUID;
	private final String playerName;
	private final ChatChannel channel;

	public MessageOptionsMenu(Player player, Menu from, Text message, UUID messageUUID, Player sender, ChatChannel ch) {
		super(player, from);
		this.channel = ch;
		this.message = message;
		this.originalMessage = message;
		this.messageUUID = messageUUID;
		this.sender = new WeakReference<>(sender);
		this.playerName = sender.getName();
		this.title = createTitle("Message");
	}

	protected Text removeOption() {
		return this.createOption("remove",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to remove the message!")),
				TextActions.executeCallback(src -> {
					if (src instanceof Player) {
						removeMessage();
					} else {
						src.sendMessage(Text.of(TextColors.RED, "You must be a player to do this!"));
					}
				}));
	}

	private void removeMessage() {
		this.message = null;
		if (this.source != null && this.source.isPresent() && this.source.get() instanceof ChatMenu) {
			ChatMenu m = (ChatMenu) this.source.get();
			m.remove(messageUUID);
			m.send();
		}
	}

	private void setMessage(Text t) {
		this.message = t;
		if (this.source != null && this.source.isPresent() && this.source.get() instanceof ChatMenu) {
			ChatMenu m = (ChatMenu) this.source.get();
			m.replace(this.messageUUID, this.message);
			m.send();
		}
	}

	protected Text editOption() {
		return this.createOption("edit", TextActions.showText(Text.of(TextColors.AQUA, "Click to edit the message!")),
				TextActions.executeCallback(src -> {
					if (src instanceof Player) {
						TextEditConversation.start((Player) src, originalMessage, t -> setMessage(t));
					} else {
						src.sendMessage(Text.of(TextColors.RED, "You must be a player to do this!"));
					}
				}));
	}

	protected Text ignoreOption() {
		return this.createOption("ignore",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to ignore " + playerName + "!")),
				TextActions.runCommand("/ignore " + playerName));
	}

	protected Text tpOption() {
		return this.createOption("teleport",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to teleport to " + playerName + "!")),
				TextActions.runCommand("/tp " + playerName));
	}

	protected Text muteOption() {
		return this.createOption("mute",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to mute " + playerName + "!")),
				TextActions.runCommand("/mute " + playerName));
	}

	protected Text kickOption() {
		return this.createOption("kick",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to kick " + playerName + "!")),
				TextActions.runCommand("/mute " + playerName));
	}

	protected Text messageOption() {
		return this.createOption("message",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to message " + playerName + "!")),
				TextActions.suggestCommand("/msg " + playerName + " "));
	}

	protected Text channelBanOption() {
		return this.createOption("channel ban",
				TextActions.showText(
						Text.of(TextColors.AQUA, "Click to ban " + playerName + " from " + channel.getName() + "!")),
				TextActions.runCommand("/ch ban " + playerName + " " + channel.getName()));

	}

	protected Text channelMuteOption() {
		return this.createOption("channel mute",
				TextActions.showText(
						Text.of(TextColors.AQUA, "Click to mute " + playerName + " in " + channel.getName() + "!")),
				TextActions.runCommand("/ch mute " + playerName));
	}

	protected Text banOption() {
		return this.createOption("ban",
				TextActions.showText(Text.of(TextColors.AQUA, "Click to ban " + playerName + "!")),
				TextActions.runCommand("/ban " + playerName));

	}

	@Override
	public List<Text> renderBody() {
		// TODO check against other plugins
		Text begin = Text.of(TextColors.GREEN, " - ");
		List<Text> out = Utils.al();
		boolean b = false;
		if (hasPerm("ray.removemessage")) {
			out.add(begin.concat(removeOption()));
			b = true;
		}
		if (hasPerm("ray.editmessage")) {
			out.add(begin.concat(editOption()));
			b = true;
		}
		if (b) {
			out.add(Text.EMPTY);
		}
		out.add(begin.concat(messageOption()));
		out.add(begin.concat(ignoreOption()));
		if (hasPerm("minecraft.command.tp")) {
			out.add(begin.concat(tpOption()));
		}
		if (hasPerm("ray.channel.ban")) {
			out.add(begin.concat(channelBanOption()));
		}
		if (hasPerm("ray.channel.mute")) {
			out.add(begin.concat(channelMuteOption()));
		}
		if (hasPerm("ray.mute")) {
			out.add(begin.concat(muteOption()));
		}
		// TODO kick option - no kick perm specified on sponge api docs
		if (hasPerm("minecraft.command.ban")) {
			out.add(begin.concat(banOption()));
		}
		return out;
	}

	@Override
	public List<Text> renderHeader() {
		return Utils.al(Text.of(TextColors.AQUA, "Now managing ").concat(originalMessage)
				.concat(Text.of(TextColors.AQUA, ".")));
	}

	@Override
	public List<Text> renderFooter() {
		return backButton().isPresent() ? Utils.al(backButton().get()) : Utils.al();
	}

	/**
	 * @return the sender
	 */
	protected WeakReference<Player> getSender() {
		return sender;
	}

}
