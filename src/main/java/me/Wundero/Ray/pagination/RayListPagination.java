package me.Wundero.Ray.pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.collect.ImmutableList;

import static me.Wundero.Ray.translation.RaySpongeCommonTranslationHelper.t;
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
			Text footer, Text padding, int linesPerPage, boolean scroll) {
		super(src, title, header, footer, padding, linesPerPage, scroll);
		List<List<Text>> pages = new ArrayList<>();
		List<Text> currentPage = new ArrayList<>();
		int currentPageLines = 0;

		for (Map.Entry<Text, Integer> ent : lines) {
			final boolean finiteLinesPerPage = getMaxContentLinesPerPage() > 0;
			final boolean willExceedPageLength = ent.getValue() + currentPageLines > getMaxContentLinesPerPage();
			final boolean currentPageNotEmpty = currentPageLines != 0;
			final boolean spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty;
			if (spillToNextPage) {
				padPage(currentPage, currentPageLines, true);
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
			if (!pages.isEmpty()) {
				// Only pad if we have a previous page
				padPage(currentPage, currentPageLines, false);
			}
			pages.add(currentPage);
		}
		this.pages = pages;
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