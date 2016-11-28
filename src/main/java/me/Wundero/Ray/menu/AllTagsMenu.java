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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.tag.SelectableTag;
import me.Wundero.Ray.utils.RayCollectors;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;

public class AllTagsMenu extends Menu {

	public AllTagsMenu(Player player) {
		super(player);
	}

	public AllTagsMenu(Player player, Menu from) {
		super(player, from);
	}

	protected List<Text> getAllTags() {
		List<SelectableTag> tags = Ray.get().getTags().getAll(Utils.hm(), SelectableTag.class);
		return tags.stream().map(tag -> {
			if (!getPlayer().isPresent()) {
				return (Text) null;
			}
			Optional<Text> ot = tag.get(Optional.of(new ParsableData().withSender(getPlayer())
					.withRecipient(getPlayer()).withObserver(getPlayer()).withClickHover(true)));
			if (ot.isPresent()) {
				return ot.get().toBuilder().onClick(TextActions.executeCallback(src -> {
					new TagMenu((Player) src, this, tag).send();
				})).build();
			} else {
				return (Text) null;
			}
		}).collect(RayCollectors.rayList());
	}

	@Override
	public List<Text> renderBody() {
		this.title = Text.of("All tags");
		return getAllTags().stream()
				.map(text -> Text.of(TextColors.GREEN, " - ").concat(text == null ? Text.of() : text))
				.collect(RayCollectors.rayList());
	}

	@Override
	public List<Text> renderHeader() {
		return Utils.al(Text.of(TextColors.AQUA, "Please select a tag type:"), Text.EMPTY);
	}

	@Override
	public List<Text> renderFooter() {
		return backButton().isPresent() ? Utils.al(backButton().get()) : Utils.al();
	}

}
