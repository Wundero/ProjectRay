package me.Wundero.Ray.menu;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.tag.SelectableTag;
import me.Wundero.Ray.utils.RayCollectors;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;

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

public class TagMenu extends Menu {

	private SelectableTag tag;

	public TagMenu(Player player, Menu from, SelectableTag tag) {
		super(player, from);
		this.tag = tag;
	}

	protected List<Text> getOptions() {
		List<Text> out = Utils.al();
		if (!getPlayer().isPresent()) {
			return out;
		}
		for (String s : tag.getObject().keySet()) {
			Optional<Text> t = tag.get(Optional.of(new ParsableData().withSender(getPlayer()).withRecipient(getPlayer())
					.withObserver(getPlayer()).withClickHover(true)));
			t.ifPresent(text -> out.add(Text.of(TextColors.AQUA, getClick(s), "\"" + s + "\": ")
					.concat(text.toBuilder().onClick(getClick(s)).build())));
		}
		out.add(Text.of(TextColors.RED, TextActions.executeCallback(src -> {
			if (src instanceof Player) {
				RayPlayer.get((Player) src).select(tag, null);
			}
			src.sendMessage(Text.of(TextColors.AQUA, "Tag reset!"));
		}), "reset"));
		return out;
	}

	protected ClickAction<?> getClick(String tagName) {
		return TextActions.executeCallback(src -> {
			Sponge.getCommandManager().process(src, "/tag select " + tag.getName() + " " + tagName);
			if (this.source.isPresent()) {
				source.get().sendSourceOrThis();
			}
		});
	}

	@Override
	public List<Text> renderBody() {
		return getOptions().stream().map(text -> Text.of(TextColors.GREEN, " - ").concat(text))
				.collect(RayCollectors.rayList());
	}

	@Override
	public List<Text> renderHeader() {
		return Utils.al(Text.of(TextColors.AQUA, "Please select a tag:"), Text.EMPTY);
	}

	@Override
	public List<Text> renderFooter() {
		return backButton().isPresent() ? Utils.al(backButton().get()) : Utils.al();
	}

}
