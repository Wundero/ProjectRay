package me.Wundero.Ray.pagination;

import static me.Wundero.Ray.translation.RaySpongeCommonTranslationHelper.t;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.collect.ImmutableList;

import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
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

class RayListPagination extends RayActivePagination {
	private final List<List<Text>> pages;

	public RayListPagination(MessageReceiver src, List<Map.Entry<Text, Integer>> lines, Text title, Text header,
			Text footer, Text padding, int linesPerPage, boolean scroll, boolean clear) {
		super(src, title, header, footer, padding, linesPerPage, scroll, clear);

		this.pages = buildPages(lines);
	}

	private List<List<Text>> buildNotScroll(List<Map.Entry<Text, Integer>> lines) {
		List<List<Text>> pages = Utils.al();
		List<Text> currentPage = Utils.al();
		int currentPageLines = 0;

		for (Map.Entry<Text, Integer> ent : lines) {
			final boolean finiteLinesPerPage = getMaxContentLinesPerPage() > 0;
			final boolean willExceedPageLength = ent.getValue() + currentPageLines > getMaxContentLinesPerPage();
			final boolean currentPageNotEmpty = currentPageLines != 0;
			final boolean spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty;
			if (spillToNextPage) {
				padPage(currentPage, currentPageLines, true, false);
				currentPageLines = 0;
				pages.add(currentPage);
				currentPage = new ArrayList<>();
			}
			currentPageLines += ent.getValue();
			currentPage.add(ent.getKey());
		}
		// last page is not yet committed
		final boolean lastPageNotEmpty = currentPageLines > 0;
		if (lastPageNotEmpty) {
			if (!pages.isEmpty() && !scroll) {
				// Only pad if we have a previous page
				padPage(currentPage, currentPageLines, false, false);
			}
			pages.add(currentPage);
		}
		return pages;
	}

	private List<List<Text>> buildScroll(List<Map.Entry<Text, Integer>> lines) {
		List<List<Text>> pages = Utils.al();
		List<Text> currentPage = Utils.al();
		int currentPageLines = 0;

		for (Map.Entry<Text, Integer> ent : lines) {
			final boolean finiteLinesPerPage = getMaxContentLinesPerPage() > 0;
			final boolean willExceedPageLength = ent.getValue() + currentPageLines > getMaxContentLinesPerPage();
			final boolean currentPageNotEmpty = currentPageLines != 0;
			final boolean spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty;
			if (spillToNextPage) {
				padPage(currentPage, currentPageLines, false, false);
				currentPageLines = 0;
				pages.add(currentPage);
				currentPage = new ArrayList<>();
			}
			currentPageLines += ent.getValue();
			currentPage.add(ent.getKey());
		}
		// last page is not yet committed
		final boolean lastPageNotEmpty = currentPageLines > 0;
		if (lastPageNotEmpty) {
			pages.add(currentPage);
		}
		int lineDiff = this.getMaxContentLinesPerPage() - currentPageLines;
		List<List<Text>> mod = Utils.al(pages, true);
		/*
		 * Note to self: This works like this:
		 * 
		 * 876/543/21-----------------------------------------------------------
		 * 876/54/321-----------------------------------------------------------
		 * 87/654/321-----------------------------------------------------------
		 * x87/654/321----------------------------------------------------------
		 */
		for (int i = pages.size() - 1; i >= 0; i--) {
			if (i == 0) {
				List<Text> c = mod.get(i);
				padPage(c, currentPageLines, false, false);
				break;
			}
			int x = lineDiff;
			List<Text> c1 = mod.get(i);
			List<Text> c2 = mod.get(i - 1);
			while (x > 0) {
				Text t = c2.remove(c2.size() - 1);
				int lu = TextUtils.getLines(t);
				c1.add(0, t);
				x -= lu;
			}
			mod.set(i, c1);
			mod.set(i - 1, c2);
		}
		return mod;
	}

	private List<List<Text>> buildPages(List<Map.Entry<Text, Integer>> map) {
		if (super.scroll) {
			return buildScroll(map);
		} else {
			return buildNotScroll(map);
		}
	}

	@Override
	protected Iterable<Text> getLines(int page) throws CommandException {
		if (this.pages.size() == 0) {
			return ImmutableList.of();
		} else if (page < 1) {
			throw new CommandException(t("Page %s does not exist!", page));
		} else if (page > this.pages.size()) {
			throw new CommandException(t("Page %s is too high", page));
		}
		return this.pages.get(page - 1);
	}

	@Override
	protected boolean hasPrevious(int page) {
		return page > 1;
	}

	@Override
	protected boolean hasNext(int page) {
		return page < this.pages.size();
	}

	@Override
	protected int getTotalPages() {
		return this.pages.size();
	}
}