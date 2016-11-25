package me.Wundero.Ray.tag;
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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.framework.player.SocialMedia;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Pseudo-enum list of all default tags
 */
public class DefaultTags {
	public static final DefaultTag SELECT_TAG = new DefaultTag().withSelectable("chat").with("default",
			Text.of(TextColors.BLACK, TextActions.showText(Text.of(TextColors.AQUA, "This is the default chat tag!")),
					"[", TextColors.AQUA, "Default", TextColors.BLACK, "]"))
			.with("admin",
					Text.of(TextColors.BLACK,
							TextActions.showText(Text.of(TextColors.AQUA, "This is the admin chat tag!")), "[",
							TextColors.DARK_RED, "Default", TextColors.BLACK, "]"))
			.build();
	// TODO the remaining social media in the enum
	public static final DefaultTag YOUTUBE_TAG = new DefaultTag()
			.withTag(new SocialMediaTag("youtube", SocialMedia.YOUTUBE));
	public static final DefaultTag TWITTER_TAG = new DefaultTag()
			.withTag(new SocialMediaTag("youtube", SocialMedia.TWITTER));

	public static void applyAll(ConfigurationNode node) {
		apply(node, SELECT_TAG, YOUTUBE_TAG, TWITTER_TAG);
	}

	public static void apply(ConfigurationNode node, DefaultTag... tags) {
		ConfigurationNode n = node.getNode("tags");
		for (DefaultTag t : tags) {
			Tag<?> tag = t.getTag();
			tag.save(n);
			n.getNode(tag.getName(), "type").setValue(tag.getClass().getName());
		}
	}

}
