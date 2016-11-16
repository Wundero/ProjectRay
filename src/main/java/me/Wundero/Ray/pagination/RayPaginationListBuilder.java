package me.Wundero.Ray.pagination;

import javax.annotation.Nullable;

import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

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

public class RayPaginationListBuilder implements PaginationList.Builder {
	private final RayPaginationService service;
	private Iterable<Text> contents;
	private Text title;
	private Text header;
	private Text footer;
	private Text paginationSpacer = Text.of("=");
	private int linesPerPage = 20;
	private boolean scroll = false;

	private PaginationList paginationList;

	public RayPaginationListBuilder(RayPaginationService service) {
		this.service = service;
	}

	@Override
	public PaginationList.Builder contents(Iterable<Text> contents) {
		this.contents = contents;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder contents(Text... contents) {
		this.contents = ImmutableList.copyOf(contents);
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder title(Text title) {
		this.title = title;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder header(@Nullable Text header) {
		this.header = header;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder footer(@Nullable Text footer) {
		this.footer = footer;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder padding(Text padding) {
		this.paginationSpacer = padding;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder linesPerPage(int linesPerPage) {
		this.linesPerPage = linesPerPage;
		return this;
	}

	public PaginationList.Builder scroll(boolean scroll) {
		this.scroll = scroll;
		return this;
	}

	@Override
	public PaginationList build() {
		Preconditions.checkNotNull(this.contents, "contents");

		if (this.paginationList == null) {
			this.paginationList = new RayPaginationList(this.service, this.contents, this.title, this.header,
					this.footer, this.paginationSpacer, this.linesPerPage, scroll);
		}
		return this.paginationList;
	}

	@Override
	public void sendTo(MessageReceiver source) {
		this.build().sendTo(source);
	}

	@Override
	public void sendTo(MessageChannel channel) {
		this.build().sendTo(channel);
	}

	@Override
	public PaginationList.Builder from(PaginationList list) {
		this.reset();
		this.contents = list.getContents();
		this.title = list.getTitle().orElse(null);
		this.header = list.getHeader().orElse(null);
		this.footer = list.getFooter().orElse(null);
		this.paginationSpacer = list.getPadding();
		this.scroll = list instanceof RayPaginationList ? ((RayPaginationList) list).scroll() : false;
		this.paginationList = null;
		return this;
	}

	@Override
	public PaginationList.Builder reset() {
		this.contents = null;
		this.title = null;
		this.header = null;
		this.footer = null;
		this.paginationSpacer = Text.of("=");
		this.scroll = false;
		this.paginationList = null;
		return this;
	}
}
