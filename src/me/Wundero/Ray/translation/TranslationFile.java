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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.locale.Locales;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.utils.Utils;

public class TranslationFile {

	//TODO test this
	private File file;
	private Map<String, String> lines = Utils.sm();
	private boolean usable = false;
	private Locale locale;
	private static Map<Locale, TranslationFile> files = Utils.sm();

	public TranslationFile(File file) {
		this.setFile(file);
		setLocale(Locale.forLanguageTag(file.getName().split(".")[0]));
		parse(file, this);
	}

	public static Translation getTranslation(final String key) {
		return new Translation() {

			@Override
			public String getId() {
				return key;
			}

			@Override
			public String get(Locale locale) {
				if (key == null) {
					return "";
				}
				String e = "";
				if (!files.containsKey(locale) || !files.get(locale).usable) {
					if (!files.containsKey(Locales.DEFAULT) || !files.get(Locales.DEFAULT).usable) {
						return e;
					}
					String g = files.get(Locales.DEFAULT).lines.get(key);
					return g == null ? e : g;
				}
				String g = files.get(locale).lines.get(key);
				return g == null ? e : g;
			}

			@Override
			public String get(Locale locale, Object... args) {
				return String.format(get(locale), args);
			}

		};
	}

	private static Task parse(final File f, final TranslationFile tf) {
		Task t = Task.builder().async().execute((task) -> {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				Stream<String> lines = reader.lines();
				Map<String, String> k = lines.filter(s -> s.contains(":"))
						.collect(Collectors.toMap(
								(Function<? super String, ? extends String>) (String in) -> in.split(":")[0],
								(Function<? super String, ? extends String>) (String in) -> in
										.replace(in.split(":")[0] + ":", "")));
				tf.lines = k;
				reader.close();
				tf.setUsable(true);
				files.put(tf.locale, tf);
			} catch (Exception e) {
				task.cancel();
				return;
			}
		}).submit(Ray.get().getPlugin());
		Ray.get().registerTask(t);
		return t;
	}

	public String put(String key, String value) {
		return lines.put(key, value);
	}

	public Locale getLocale() {
		return locale;
	}

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isUsable() {
		return usable;
	}

	private void setUsable(boolean usable) {
		this.usable = usable;
	}

	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}
}
