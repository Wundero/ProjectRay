package me.Wundero.Ray.pagination;
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

import static me.Wundero.Ray.translation.RaySpongeCommonTranslationHelper.t;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class RayIterablePagination extends RayActivePagination {

	private final PeekingIterator<Map.Entry<Text, Integer>> countIterator;
	private int lastPage;

	public RayIterablePagination(MessageReceiver src, Iterable<Map.Entry<Text, Integer>> counts, Text title,
			Text header, Text footer, Text padding, int lines) {
		super(src, title, header, footer, padding, lines);
		this.countIterator = Iterators.peekingIterator(counts.iterator());
	}

	@Override
	protected Iterable<Text> getLines(int page) throws CommandException {
		if (!this.countIterator.hasNext()) {
			throw new CommandException(t("Already at end of iterator"));
		}

		if (page <= this.lastPage) {
			throw new CommandException(t("Cannot go backward in an IterablePagination"));
		} else if (page > this.lastPage + 1) {
			getLines(page - 1);
		}
		this.lastPage = page;

		if (getMaxContentLinesPerPage() <= 0) {
			return Lists.newArrayList(Iterators.transform(this.countIterator, input -> input.getKey()));
		}

		List<Text> ret = new ArrayList<>(getMaxContentLinesPerPage());
		int addedLines = 0;
		while (addedLines <= getMaxContentLinesPerPage()) {
			if (!this.countIterator.hasNext()) {
				// Pad the last page, but only if it isn't the first.
				if (page > 1) {
					padPage(ret, addedLines, false);
				}
				break;
			}
			if (addedLines + this.countIterator.peek().getValue() > getMaxContentLinesPerPage()) {
				// Add the continuation marker, pad if required
				padPage(ret, addedLines, true);
				break;
			}
			Map.Entry<Text, Integer> ent = this.countIterator.next();
			ret.add(ent.getKey());
			addedLines += ent.getValue();
		}
		return ret;
	}

	@Override
	protected boolean hasPrevious(int page) {
		return false;
	}

	@Override
	protected boolean hasNext(int page) {
		return page == getCurrentPage() && this.countIterator.hasNext();
	}

	@Override
	protected int getTotalPages() {
		return -1;
	}

	@Override
	public void previousPage() throws CommandException {
		throw new CommandException(t("Cannot go backwards in a streaming pagination"));
	}
}
