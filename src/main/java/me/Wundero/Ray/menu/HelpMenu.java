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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import me.Wundero.Ray.utils.Utils;

public class HelpMenu extends Menu {

	private PaginationList list;

	public HelpMenu(Player player, PaginationList list) {
		super(player);
		this.list = list;
		list.getTitle().ifPresent(t -> this.setTitle(t));
	}

	@Override
	public void send() {
		this.getPlayer().ifPresent(list::sendTo);
	}

	@Override
	public List<Text> renderBody() {
		List<Text> l = Utils.al();
		for (Text t : list.getContents()) {
			l.add(t);
		}
		return l;
	}

	@Override
	public List<Text> renderHeader() {
		Optional<Text> h = list.getHeader();
		return h.isPresent() ? Utils.al(h.get()) : Utils.al();
	}

	@Override
	public List<Text> renderFooter() {
		Optional<Text> h = list.getFooter();
		return h.isPresent() ? Utils.al(h.get()) : Utils.al();
	}

}
