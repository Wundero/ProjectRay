package me.Wundero.Ray.commands;
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
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.utils.Utils;

/**
 * Mute a player.
 */
public class MuteCommand implements CommandExecutor {
	@Listener
	public void onChat(MessageChannelEvent.Chat e) {
		Cause c = e.getCause();
		if (!c.containsType(Player.class)) {
			return;
		}
		if (c.first(Player.class).isPresent()) {
			Player p = c.first(Player.class).get();
			if (muted.contains(p.getUniqueId())) {
				e.setCancelled(true);
				e.setMessageCancelled(true);
			}
		}
	}

	private List<UUID> muted = Utils.sl();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src.hasPermission("ray.mute"))) {
			throw new CommandException(Text.of(TextColors.RED, "You are not allowed to do that!"));
		}
		Optional<Player> name = args.getOne("target");
		if (!name.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "You must specify a target!"));
		}
		boolean cur = toggle(name.get().getUniqueId());
		String s = "no" + (cur ? "w" : " longer");
		Utils.send(src, Text.of(TextColors.AQUA, name.get().getName() + " is " + s + " muted!"));
		return CommandResult.success();
	}

	private boolean toggle(UUID u) {
		if (muted.contains(u)) {
			muted.remove(u);
			return false;
		} else {
			muted.add(u);
			return true;
		}
	}

}
