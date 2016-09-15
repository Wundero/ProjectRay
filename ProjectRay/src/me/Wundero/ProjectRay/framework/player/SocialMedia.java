package me.Wundero.ProjectRay.framework.player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.ProjectRay.utils.TextUtils;

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

public enum SocialMedia {
	YOUTUBE(Text.of(TextColors.DARK_RED, "[" + '\u25b6' + "]"), name -> {
		try {
			return new URL("https://www.youtube.com/user/" + name + "/");
		} catch (MalformedURLException e) {
			return null;
		}
	}), TWITTER(Text.of(TextColors.AQUA, "[(.)>]"), name -> {
		try {
			return new URL("https://twitter.com/" + name + "/");
		} catch (MalformedURLException e) {
			return null;
		}
	}), INSTAGRAM(Text.of(TextColors.LIGHT_PURPLE, "[o]"), name -> {
		try {
			return new URL("https://www.instagram.com/" + name + "/");
		} catch (MalformedURLException e) {
			return null;
		}
	}), PINTEREST(Text.of(TextColors.RED, "[P]"), name -> {
		try {
			return new URL("https://www.pinterest.com/" + name + "/");
		} catch (MalformedURLException e) {
			return null;
		}
	}), TUMBLR(Text.of(TextColors.BLUE, "[t]"), name -> {
		try {
			return new URL("http://" + name + ".tumblr.com/");
		} catch (MalformedURLException e) {
			return null;
		}
	}), VINE(Text.of(TextColors.GREEN, "[V]"), name -> {
		try {
			return new URL("https://vine.co/" + name + "/");
		} catch (MalformedURLException e) {
			return null;
		}
	}, s -> s.replace(" ", "."));

	private Function<String, URL> userApplier;
	private Function<String, String> sanitizer;
	private Text tag;

	SocialMedia(Text t, Function<String, URL> app, Function<String, String> sanitizer) {
		this.userApplier = app;
		this.sanitizer = sanitizer;
		this.tag = t;
	}

	SocialMedia(Text t, Function<String, URL> app) {
		this(t, app, s -> s);
	}

	public Text getTag() {
		return tag;
	}

	public Text formatTag(URL u) {
		return TextUtils
				.apply(getTag().toBuilder(), Optional.of(TextActions.openUrl(u)), Optional.empty(), Optional.empty())
				.build();
	}

	public URL apply(UUID u) {
		if (RayPlayer.get(u) != null) {
			return apply(RayPlayer.get(u));
		} else {
			return null;
		}
	}

	public URL apply(RayPlayer r) {
		return apply(r.getUser());
	}

	public URL apply(User u) {
		return apply(u.getName());
	}

	public URL apply(String name) {
		return userApplier.apply(sanitizer.apply(name));
	}
}
