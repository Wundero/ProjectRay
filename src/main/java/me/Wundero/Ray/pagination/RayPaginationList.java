package me.Wundero.Ray.pagination;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMessageFormatting;
import org.spongepowered.api.command.source.ProxySource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import me.Wundero.Ray.utils.TextUtils;

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

public class RayPaginationList implements PaginationList {

	private final RayPaginationService service;
	private Iterable<Text> contents;
	private Optional<Text> title;
	private Optional<Text> header;
	private Optional<Text> footer;
	private Text paginationSpacer;
	private int linesPerPage;
	private boolean scrolling = false, clear = false;

	public RayPaginationList(RayPaginationService service, Iterable<Text> contents, Text title, Text header,
			Text footer, Text paginationSpacer, int linesPerPage) {
		this.service = service;
		this.contents = contents;
		this.title = Optional.ofNullable(title);
		this.header = Optional.ofNullable(header);
		this.footer = Optional.ofNullable(footer);
		this.paginationSpacer = paginationSpacer;
		this.linesPerPage = linesPerPage;
	}

	public RayPaginationList(RayPaginationService service, Iterable<Text> contents, Text title, Text header,
			Text footer, Text paginationSpacer, int linesPerPage, boolean scroll, boolean clear) {
		this(service, contents, title, header, footer, paginationSpacer, linesPerPage);
		this.scrolling = scroll;
		this.clear = clear;
	}

	public boolean scroll() {
		return scrolling;
	}

	@Override
	public Iterable<Text> getContents() {
		return this.contents;
	}

	@Override
	public Optional<Text> getTitle() {
		return this.title;
	}

	@Override
	public Optional<Text> getHeader() {
		return this.header;
	}

	@Override
	public Optional<Text> getFooter() {
		return this.footer;
	}

	@Override
	public Text getPadding() {
		return this.paginationSpacer;
	}

	@Override
	public int getLinesPerPage() {
		return this.linesPerPage;
	}

	@Override
	public void sendTo(final MessageReceiver receiver) {
		Preconditions.checkNotNull(this.contents, "contents");
		Preconditions.checkNotNull(receiver, "source");
		this.service.registerCommandOnce();

		MessageReceiver realSource = receiver;
		while (realSource instanceof ProxySource) {
			realSource = ((ProxySource) realSource).getOriginalSource();
		}
		Iterable<Map.Entry<Text, Integer>> counts = StreamSupport.stream(this.contents.spliterator(), false)
				.map(input -> {
					int lines = TextUtils.getLines(input);
					return Maps.immutableEntry(input, lines);
				}).collect(Collectors.toList());

		Text title = this.title.orElse(null);
		if (title != null) {
			title = TextUtils.center(title, this.paginationSpacer);
		}

		RayActivePagination pagination;
		if (this.contents instanceof List) { // If it started out as a list,
												// it's probably reasonable to
												// copy it to another list
			pagination = new RayListPagination(receiver, ImmutableList.copyOf(counts), title, this.header.orElse(null),
					this.footer.orElse(null), this.paginationSpacer, linesPerPage, scrolling, clear);
		} else {
			pagination = new RayIterablePagination(receiver, counts, title, this.header.orElse(null),
					this.footer.orElse(null), this.paginationSpacer, linesPerPage, scrolling, clear);
		}

		this.service.getPaginationState(receiver, true).put(pagination);
		try {
			if (!scrolling) {
				pagination.nextPage();
			} else {
				pagination.specificPage(pagination.getTotalPages());
			}
		} catch (CommandException e) {
			receiver.sendMessage(CommandMessageFormatting.error(e.getText()));
		}
	}
}
