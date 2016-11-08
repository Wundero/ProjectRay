package me.Wundero.Ray.translation;
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.translation.locale.Locales;

import me.Wundero.Ray.Ray;

/**
 * Internal message mappings for default messages.
 */
public class I18n {
	private static I18n instance;
	private final transient Locale defaultLocale = Locales.DEFAULT;
	private transient Locale currentLocale;
	private transient ResourceBundle localeBundle, customBundle;
	private final transient ResourceBundle defaultBundle;
	private static final Pattern NODOUBLEMARK = Pattern.compile("''");
	private transient Map<String, MessageFormat> messageFormatCache = new HashMap<String, MessageFormat>();
	private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {

		@Override
		protected Object handleGetObject(String key) {
			return null;
		}

		@Override
		public Enumeration<String> getKeys() {
			return null;
		}

	};

	public I18n() {
		defaultBundle = getBundleSafe(defaultLocale);
		localeBundle = defaultBundle;
		customBundle = NULL_BUNDLE;
	}

	public void onEnable() {
		instance = this;
	}

	public void onDisable() {
		instance = null;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public static Text t(final String string, final Object... objects) {
		return TextSerializers.FORMATTING_CODE.deserialize(tl(string, objects));
	}

	public static String tl(final String string, final Object... objects) {
		if (instance == null) {
			return "";
		}
		if (objects.length == 0) {
			return NODOUBLEMARK.matcher(instance.translate(string)).replaceAll("'");
		} else {
			return instance.format(string, objects);
		}
	}

	public String format(final String string, final Object... objects) {
		String format = translate(string);
		MessageFormat messageFormat = messageFormatCache.get(format);
		if (messageFormat == null) {
			try {
				messageFormat = new MessageFormat(format);
			} catch (IllegalArgumentException e) {
				format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
				messageFormat = new MessageFormat(format);
			}
			messageFormatCache.put(format, messageFormat);
		}
		return messageFormat.format(objects);
	}

	private String translate(final String string) {
		try {
			try {
				return customBundle.getString(string);
			} catch (MissingResourceException ex) {
				return localeBundle.getString(string);
			}
		} catch (MissingResourceException ex) {
			return defaultBundle.getString(string);
		}
	}

	private static ResourceBundle getBundleSafe(Locale l) {
		try {
			return getBundle(l);
		} catch (Exception e) {
			return NULL_BUNDLE;
		}
	}

	public void updateLocale(final String loc) {
		if (loc != null && !loc.isEmpty()) {
			final String[] parts = loc.split("[_\\.]");
			if (parts.length == 1) {
				currentLocale = new Locale(parts[0]);
			}
			if (parts.length == 2) {
				currentLocale = new Locale(parts[0], parts[1]);
			}
			if (parts.length == 3) {
				currentLocale = new Locale(parts[0], parts[1], parts[2]);
			}
		}
		ResourceBundle.clearCache();
		messageFormatCache = new HashMap<String, MessageFormat>();
		try {
			localeBundle = getBundle(currentLocale);
		} catch (Exception ex) {
			localeBundle = NULL_BUNDLE;
		}

		try {
			customBundle = getBundle(currentLocale, Ray.get().getPlugin().getConfigDir().toUri());
		} catch (Exception ex) {
			customBundle = NULL_BUNDLE;
		}
	}

	private static URL getFolderUrl(Asset a) throws MalformedURLException, URISyntaxException {
		return new File(a.getUrl().toURI()).getParentFile().toURI().toURL();
	}

	private static ResourceBundle getBundle(Locale l, URI uri) throws Exception {
		URL[] urls = { uri.toURL() };
		ClassLoader loader = new URLClassLoader(urls);
		return ResourceBundle.getBundle("messages", Locale.getDefault(), loader);
	}

	private static ResourceBundle getBundle(Locale l) throws Exception {
		URL[] urls = { getFolderUrl(Sponge.getAssetManager().getAsset(Ray.get().getPlugin(), "messages").get()) };
		ClassLoader loader = new URLClassLoader(urls);
		return ResourceBundle.getBundle("messages", Locale.getDefault(), loader);
	}
}
