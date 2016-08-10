package me.Wundero.ProjectRay.commands.channel;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.framework.RayPlayer;
import me.Wundero.ProjectRay.framework.channel.Role;

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

public class ChannelRoleCommand implements CommandExecutor {

	// /ch role [player] [set <role>]

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean usetarget = args.hasAny("target");
		if (!(src instanceof Player) && !usetarget) {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to view your role in a channel!"));
			return CommandResult.success();
		}
		Player target = null;
		if (usetarget) {
			target = (Player) args.getOne("target").get();
		} else {
			target = (Player) src;
		}
		boolean set = args.hasAny("set");
		String y = usetarget ? target.getName() + "'s" : "Your";
		String y2 = usetarget ? target.getName() + "'s" : "your";
		if (!set) {
			Role r = RayPlayer.get(target).getActiveChannel().getMembersCollection().get(target.getUniqueId())
					.getRole();
			String n = RayPlayer.get(target).getActiveChannel().getName();
			Text t = Text.of(TextColors.AQUA,
					y + " role in channel " + n + " is " + StringUtils.capitalize(r.name().toLowerCase()) + ".");
			src.sendMessage(t);
			return CommandResult.success();
		}
		if (!args.hasAny("role")) {
			src.sendMessage(Text.of(TextColors.RED, "You must choose a role to set!"));
			return CommandResult.success();
		}
		String role = (String) args.getOne("role").get();
		Role r = Role.valueOf(role.toUpperCase());
		if(r==null) {
			src.sendMessage(Text.of(TextColors.RED, "That is not a valid role!"));
			return CommandResult.success();
		}
		RayPlayer.get(target).getActiveChannel().setMemberRole(target, r);
		src.sendMessage(Text.of(TextColors.AQUA, "You have successfully set " + y2 + " role to " + role + "."));
		return CommandResult.success();
	}

}
