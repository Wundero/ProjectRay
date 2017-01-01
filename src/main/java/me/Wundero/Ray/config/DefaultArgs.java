package me.Wundero.Ray.config;
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
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import me.Wundero.Ray.utils.TextUtils;

/**
 * Arguments made to be swiftly used when loading templates into the config.
 */
public class DefaultArgs {
	// random default args
	/**
	 * Player displayname
	 */
	public static final DefaultArg DISPLAYNAME = new DefaultArg(TextTemplate.arg("displayname").color(TextColors.AQUA),
			TextUtils.suggestTemplate(TextTemplate.of("/msg ", TextTemplate.arg("displayname").build(), " ")),
			TextUtils.showTemplate(TextTemplate.of(Text.builder("Name: ").color(TextColors.GRAY).build(),
					TextTemplate.arg("displayname").color(TextColors.AQUA).build(), "\n",
					Text.builder("Say hello!").color(TextColors.GOLD).build())));
	/**
	 * Recipient displayname
	 */
	public static final DefaultArg RECIP_DISPLAYNAME = new DefaultArg(
			TextTemplate.arg("recip_displayname").color(TextColors.AQUA),
			TextUtils.suggestTemplate(TextTemplate.of("/msg ", TextTemplate.arg("recip_displayname").build(), " ")),
			TextUtils.showTemplate(TextTemplate.of(Text.builder("Name: ").color(TextColors.GRAY).build(),
					TextTemplate.arg("recip_displayname").color(TextColors.AQUA).build(), "\n", Text
							.builder("Say hello!").color(TextColors.GOLD).build())));
}
