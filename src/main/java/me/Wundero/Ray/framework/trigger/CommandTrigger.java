package me.Wundero.Ray.framework.trigger;
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
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandTrigger extends Trigger {

	/*
	 * TODO more opts
	 * filter cmds for args/names/aliases
	 * more stuff
	 * target choosing - sender, cmd targets, cmd target[x], all?
	 */

	@Setting
	private String command;
	@Setting
	private boolean cancel = false;

	public CommandTrigger() {
		Sponge.getEventManager().registerListeners(Ray.get().getPlugin(), this);
	}

	@Listener
	public void onCommand(SendCommandEvent event) {
		Map<String, Object> vars = Utils.hm();
		vars.put("command", event.getCommand());
		vars.put("args", event.getArguments());
		vars.put("full command", "/" + event.getCommand() + event.getArguments());
		ParsableData d = new ParsableData();
		CommandSource src = event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole());
		if (src instanceof Player) {
			Player p = (Player) src;
			d.withSender(p);
		}
		List<Player> targets = parseForPlayer(event.getArguments());
		d.withKnown(vars);
		if (trigger(d, targets) > 0) {
			if (cancel) {
				event.setCancelled(true);
			}
		}
	}

	private static List<Player> parseForPlayer(String args) {
		if (!args.contains(" ")) {
			Optional<Player> p = Sponge.getServer().getPlayer(args);
			if (p.isPresent()) {
				List<Player> o = Utils.al();
				o.add(p.get());
				return o;
			} else {
				return Utils.al();
			}
		}
		List<Player> o = Utils.al();
		for (String s : args.split(" ")) {
			Optional<Player> p = Sponge.getServer().getPlayer(s);
			if (p.isPresent()) {
				o.add(p.get());
			}
		}
		return o;
	}

}
