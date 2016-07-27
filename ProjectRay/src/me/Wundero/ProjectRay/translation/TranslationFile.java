package me.Wundero.ProjectRay.translation;
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
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.Ray;

public class TranslationFile {
	private File file;
	private Map<String, String> lines = Maps.newHashMap();
	private boolean usable = false;
	private Locale locale;
	private static Map<Locale, TranslationFile> files = Maps.newHashMap();

	public TranslationFile(File file) {
		this.setFile(file);
		setLocale(Locale.forLanguageTag(file.getName()));
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
				return files.get(locale).lines.get(key);
			}

			@Override
			public String get(Locale locale, Object... args) {
				return String.format(files.get(locale).lines.get(key), args);
			}

		};
	}

	private Task parse(final File f, final TranslationFile tf) {
		Task t = Task.builder().async().execute((task) -> {
			try {
				Scanner scn = new Scanner(f);
				while (scn.hasNextLine()) {
					put(scn.nextLine());
				}
				scn.close();
				setUsable(true);
				files.put(locale, tf);
			} catch (Exception e) {
				task.cancel();
				return;
			}
			setUsable(true);
		}).submit(Ray.get().getPlugin());
		return t;
	}

	private void put(String toSplit) {
		int index = toSplit.indexOf(':');
		String key = toSplit.substring(0, index);
		String value = toSplit.substring(index + 1);
		lines.put(key, value);
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
