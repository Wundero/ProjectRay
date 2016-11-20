package me.Wundero.Ray.pagination;

import static me.Wundero.Ray.translation.RaySpongeCommonTranslationHelper.t;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

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

abstract class RayActivePagination {

	private static final Text SLASH_TEXT = Text.of("/");
	private static final Text DIVIDER_TEXT = Text.of(" ");
	private static final Text CONTINUATION_TEXT = t("...");
	private final WeakReference<MessageReceiver> src;
	private final UUID id = UUID.randomUUID();
	private final Text nextPageText;
	private final Text prevPageText;
	@Nullable
	private final Text title;
	@Nullable
	private final Text header;
	@Nullable
	private final Text footer;
	private int currentPage;
	private final int maxContentLinesPerPage;
	private final Text padding;
	protected final boolean clearChat;
	protected final boolean scroll;

	public RayActivePagination(MessageReceiver src, Text title, Text header, Text footer, Text padding,
			int linesPerPage, boolean scroll, boolean clearchat) {
		this.src = new WeakReference<>(src);
		this.clearChat = clearchat;
		this.title = title;
		this.header = header;
		this.footer = footer;
		this.padding = padding;
		this.scroll = scroll;
		this.nextPageText = t(scroll ? "Scroll down" : "�").toBuilder().color(TextColors.BLUE)
				.style(TextStyles.UNDERLINE)
				.onClick(TextActions.runCommand("/pagination " + this.id.toString() + " next"))
				.onHover(TextActions.showText(Text.of("/page next"))).onShiftClick(TextActions.insertText("/page next"))
				.build();
		this.prevPageText = t(scroll ? "Scroll up" : "�").toBuilder().color(TextColors.BLUE).style(TextStyles.UNDERLINE)
				.onClick(TextActions.runCommand("/pagination " + this.id.toString() + " prev"))
				.onHover(TextActions.showText(Text.of("/page prev"))).onShiftClick(TextActions.insertText("/page prev"))
				.build();
		int maxContentLinesPerPage = linesPerPage - 1;
		if (title != null) {
			maxContentLinesPerPage -= TextUtils.getLines(title);
		}
		if (header != null) {
			maxContentLinesPerPage -= TextUtils.getLines(header);
		}
		if (footer != null) {
			maxContentLinesPerPage -= TextUtils.getLines(footer);
		}
		this.maxContentLinesPerPage = maxContentLinesPerPage;

	}

	public UUID getId() {
		return this.id;
	}

	protected abstract Iterable<Text> getLines(int page) throws CommandException;

	protected abstract boolean hasPrevious(int page);

	protected abstract boolean hasNext(int page);

	protected abstract int getTotalPages();

	public void nextPage() throws CommandException {
		specificPage(this.currentPage + 1);
	}

	public void previousPage() throws CommandException {
		specificPage(this.currentPage - 1);
	}

	public void currentPage() throws CommandException {
		specificPage(this.currentPage);
	}

	protected int getCurrentPage() {
		return this.currentPage;
	}

	protected int getMaxContentLinesPerPage() {
		return this.maxContentLinesPerPage;
	}

	public void specificPage(int page) throws CommandException {
		MessageReceiver src = this.src.get();
		if (src == null) {
			throw new CommandException(t("Source for pagination %s is no longer active!", getId()));
		}
		this.currentPage = page;

		List<Text> toSend = new ArrayList<>();
		Text title = this.title;
		if (title != null) {
			toSend.add(title);
		}
		if (this.header != null) {
			toSend.add(this.header);
		}

		for (Text line : getLines(page)) {
			toSend.add(line);
		}

		Text footer = calculateFooter(page);
		if (footer != null) {
			toSend.add(TextUtils.center(footer, this.padding));
		}
		if (this.footer != null) {
			toSend.add(this.footer);
		}
		if (clearChat) {
			toSend.addAll(0, Utils.fill(Text.of(" "), (t, i) -> t, 100, true));
		}
		src.sendMessages(toSend);
	}

	protected Text calculateFooter(int currentPage) {
		boolean hasPrevious = hasPrevious(currentPage);
		boolean hasNext = hasNext(currentPage);

		Text.Builder ret = Text.builder();
		if (hasPrevious) {
			ret.append(this.prevPageText).append(DIVIDER_TEXT);
		} else {
			ret.append(Text.of("�")).append(DIVIDER_TEXT);
		}
		boolean needsDiv = false;
		int totalPages = getTotalPages();
		if (totalPages > 1) {
			ret.append(Text.of(TextActions.showText(Text.of("/page " + currentPage)),
					TextActions.runCommand("/pagination " + this.id + ' ' + currentPage),
					TextActions.insertText("/page " + currentPage), currentPage))
					.append(SLASH_TEXT)
					.append(Text.of(TextActions.showText(Text.of("/page " + totalPages)),
							TextActions.runCommand("/pagination " + this.id + ' ' + totalPages),
							TextActions.insertText("/page " + totalPages), totalPages));
			needsDiv = true;
		}
		if (hasNext) {
			if (needsDiv) {
				ret.append(DIVIDER_TEXT);
			}
			ret.append(this.nextPageText);
		} else {
			if (needsDiv) {
				ret.append(DIVIDER_TEXT);
			}
			ret.append(Text.of("�"));
		}
		if (this.title != null) {
			ret.color(this.title.getColor());
			ret.style(this.title.getStyle());
		}
		return ret.build();
	}

	protected void padPage(final List<Text> currentPage, final int currentPageLines, final boolean addContinuation,
			final boolean reverse) {
		final int maxContentLinesPerPage = getMaxContentLinesPerPage();
		for (int i = currentPageLines; i < maxContentLinesPerPage; i++) {
			if (addContinuation && i == maxContentLinesPerPage - 1) {
				currentPage.add(CONTINUATION_TEXT);
			} else {
				if (!reverse) {
					currentPage.add(0, Text.EMPTY);
				} else {
					currentPage.add(Text.EMPTY);
				}
			}
		}
	}
}