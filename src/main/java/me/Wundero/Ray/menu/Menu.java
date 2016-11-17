package me.Wundero.Ray.menu;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.pagination.RayPaginationListBuilder;
import me.Wundero.Ray.translation.Translator;
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

public abstract class Menu {

	protected static final int MAX_LINES = 20;

	protected WeakReference<Player> holder;
	protected UUID holderUUID;
	protected Translator translator;
	protected Optional<Menu> source;
	protected boolean fillSpacesFromTop = false;
	protected boolean scrolling = false;
	protected Text title = null;
	protected Text pathTitle = Text.of();

	public void setTitle(Text title) {
		this.title = title;
	}

	public abstract List<Text> renderBody();

	public abstract List<Text> renderHeader();

	public abstract List<Text> renderFooter();

	public Menu(Player player) {
		this.holder = new WeakReference<Player>(player);
		this.holderUUID = player.getUniqueId();
		this.translator = new Translator(player);
		this.source = Optional.empty();
	}

	public Menu(Player player, Menu from) {
		this(player);
		this.source = Optional.of(from);
	}

	private final void paginate(List<Text> h, List<Text> b, List<Text> f, boolean scroll) {
		Text header = TextUtils.join(h, Text.of("\n"));
		Text footer = TextUtils.join(f, Text.of("\n"));
		PaginationList.Builder builder = Ray.get().getPaginationService().builder().contents(b)
				.padding(Text.of('\u2500'));
		builder.header(header).footer(footer);
		if (source.isPresent()) {
			pathTitle = source.get().pathTitle;
			pathTitle = pathTitle.concat(Text.of(TextColors.GRAY, " / ")).concat(title);
			builder.title(pathTitle);
		} else {
			builder.title(title);
		}
		if (builder instanceof RayPaginationListBuilder) {
			builder = ((RayPaginationListBuilder) builder).scroll(scroll);
		}
		builder.sendTo(getPlayer().orElseThrow(() -> new IllegalArgumentException("Player must be online!")));
	}

	public Optional<Player> getPlayer() {
		if (holder.get() == null) {
			Optional<User> u = Utils.getUser(holderUUID);
			if (!u.isPresent()) {
				return Optional.empty();
			}
			User us = u.get();
			if (!us.isOnline()) {
				return Optional.empty();
			}
			Optional<Player> p = us.getPlayer();
			p.ifPresent(player -> holder = new WeakReference<Player>(player));
			return p;
		} else {
			return Utils.wrap(holder.get());
		}
	}

	protected Text createTitle(String title) {
		return Text.of(TextColors.GOLD, resend(), title);
	}

	protected Text createURL(String name, URL reference, HoverAction<?> display) {
		return Text.builder(name).color(TextColors.AQUA).onHover(display).onClick(TextActions.openUrl(reference))
				.build();
	}

	protected Text createOption(String option, TextAction<?>... actions) {
		return TextUtils.applyAll(Text.builder(option).color(TextColors.GREEN), actions).build();
	}

	protected final Optional<Text> backButton() {
		if (!source.isPresent()) {
			return Optional.empty();
		}
		Text back = translator.t("menu.back");
		Text.Builder builder = back.toBuilder();
		toSource().ifPresent(act -> builder.onClick(act));
		return Optional.of(builder.build());
	}

	protected final Text refreshButton() {
		Text refresh = translator.t("menu.refresh");
		return refresh.toBuilder().onClick(resend()).build();
	}

	protected final Optional<ClickAction<?>> toSource() {
		if (source.isPresent()) {
			return Optional.of(source.get().resend());
		} else {
			return Optional.empty();
		}
	}

	protected final ClickAction<?> resend() {
		return TextActions.executeCallback(source -> {
			send();
		});
	}

	protected final ClickAction<?> runCommandAndRefreshMenuTextAction(String command) {
		return TextActions.executeCallback(source -> {
			runCommandAndRefreshMenu(command);
		});
	}

	protected final ClickAction<?> suggestCommand(String command) {
		return TextActions.suggestCommand((command.startsWith("/") ? "" : "/") + command);
	}

	protected final ClickAction<?> goToMenuTextAction(Class<? extends Menu> menu) {
		final Menu from = this;
		return TextActions.executeCallback(source -> {
			try {
				menu.getConstructor(Player.class, Menu.class).newInstance(holder, from).send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	protected final void runCommandAndRefreshMenu(String command) {
		Sponge.getCommandManager().process(
				getPlayer().orElseThrow(() -> new IllegalArgumentException("Player must be online!")), command);
		send();
	}

	public void send() {
		List<Text> header = renderHeader();
		List<Text> body = renderBody();
		List<Text> footer = renderFooter();
		int lb = MAX_LINES - header.size() - footer.size();
		if (lb <= 0) {
			throw new IllegalArgumentException("Header and footer must not be larger than the page!");
		}
		int spaces = lb - body.size();
		while (spaces > 0) {
			if (fillSpacesFromTop) {
				body.add(0, Text.of(" "));
			} else {
				body.add(Text.of(" "));
			}
			spaces--;
		}
		paginate(header, body, footer, scrolling);
	}

}
