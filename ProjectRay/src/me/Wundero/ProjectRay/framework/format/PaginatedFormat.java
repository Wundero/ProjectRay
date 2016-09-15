package me.Wundero.ProjectRay.framework.format;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.ProjectRay.conversation.ConversationContext;
import me.Wundero.ProjectRay.conversation.Prompt;
import me.Wundero.ProjectRay.utils.TextUtils;
import me.Wundero.ProjectRay.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;

public class PaginatedFormat extends Format {

	private Text title, header, footer, padding;
	private Format internal;

	public PaginatedFormat(ConfigurationNode node, Format internal) {
		super(node);
		if (node == null) {
			return;
		}
		this.internal = internal;
		String t = node.getNode("title").getString();
		if (t != null) {
			title = TextSerializers.FORMATTING_CODE.deserialize(t);
		}
		t = node.getNode("header").getString();
		if (t != null) {
			header = TextSerializers.FORMATTING_CODE.deserialize(t);
		}
		t = node.getNode("footer").getString();
		if (t != null) {
			footer = TextSerializers.FORMATTING_CODE.deserialize(t);
		}
		t = node.getNode("padding").getString();
		if (t != null) {
			padding = TextSerializers.FORMATTING_CODE.deserialize(t);
		}
	}

	@Override
	public Prompt getConversationBuilder(Prompt returnTo, ConversationContext context) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Format> Optional<T> getInternal(Class<T> clazz) {
		if (!hasInternal(clazz)) {
			return Optional.empty();
		}
		if (internal.getClass().equals(clazz)) {
			return Optional.of((T) internal);
		}
		return internal.getInternal(clazz);
	}

	@Override
	public boolean hasInternal(Class<? extends Format> clazz) {
		if (internal.getClass().equals(clazz)) {
			return true;
		}
		return internal.hasInternal(clazz);
	}

	private PaginationList paginate(Text topage) {
		PaginationService s = Sponge.getServiceManager().provide(PaginationService.class)
				.orElseThrow(() -> new NullPointerException("Pagination disabled!"));
		PaginationList.Builder b = s.builder();
		if (title != null) {
			b.title(title);
		}
		if (header != null) {
			b.header(header);
		}
		if (footer != null) {
			b.footer(footer);
		}
		if (padding != null) {
			b.padding(padding);
		}
		return b.contents(TextUtils.newlines(topage)).build();
	}

	@Override
	public boolean send(Function<Text, Boolean> f, Map<String, Object> args) {
	}

	@Override
	public boolean send(Function<Text, Boolean> f, ParsableData data) {
	}

}
