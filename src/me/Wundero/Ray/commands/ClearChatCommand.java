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

import static org.spongepowered.api.text.format.TextColors.AQUA;
import static org.spongepowered.api.text.format.TextColors.BLUE;
import static org.spongepowered.api.text.format.TextColors.DARK_AQUA;
import static org.spongepowered.api.text.format.TextColors.DARK_BLUE;
import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.DARK_PURPLE;
import static org.spongepowered.api.text.format.TextColors.DARK_RED;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextColors.GREEN;
import static org.spongepowered.api.text.format.TextColors.LIGHT_PURPLE;
import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextColors.YELLOW;

import java.util.List;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.utils.Utils;

public class ClearChatCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!src.hasPermission("ray.clearchat")) {
			src.sendMessage(Text.of(TextColors.RED, "You are not allowed to do that!"));
			return CommandResult.success();
		}
		String cleartype = (String) args.getOne("clear").orElse("empty");
		for (Player p : Sponge.getServer().getOnlinePlayers()) {
			p.sendMessages(getClear(cleartype.trim().toLowerCase()));
		}
		return CommandResult.success();
	}

	private static List<Text> getClear(String type) {
		if (type.equalsIgnoreCase("solid")) {
			TextColor col = Utils.randomColor();
			return Utils.fill(BLOCKS, (text, frame) -> text.toBuilder().color(col).build(), 100, false);
		}
		List<Text> out = clears.get(type);
		if (out == null) {
			return clears.get("empty");
		}
		return out;
	}

	private static Map<String, List<Text>> clears = Utils.sm();
	private static Map<Integer, TextColor> rainCols = Utils.sm();
	private static final Text BLOCKS = Text.of("███████████████████████████████████");

	static {
		clears.put("empty", Utils.fill(Text.of(" "), (t, i) -> t, 100, true));
		rainCols.put(0, AQUA);
		rainCols.put(1, BLUE);
		rainCols.put(2, DARK_BLUE);
		rainCols.put(3, DARK_PURPLE);
		rainCols.put(4, LIGHT_PURPLE);
		rainCols.put(5, DARK_RED);
		rainCols.put(6, RED);
		rainCols.put(7, GOLD);
		rainCols.put(8, YELLOW);
		rainCols.put(9, GREEN);
		rainCols.put(10, DARK_GREEN);
		rainCols.put(11, DARK_AQUA);
		clears.put("rainbow", Utils.fill(BLOCKS, (text, frame) -> {
			int col = frame % 12;
			return text.toBuilder().color(rainCols.get(col)).build();
		}, 100, false));
	}
}
