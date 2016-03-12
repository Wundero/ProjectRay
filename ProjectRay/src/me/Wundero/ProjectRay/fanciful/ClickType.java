package me.Wundero.ProjectRay.fanciful;

import java.util.List;
import java.util.regex.Pattern;

import me.Wundero.ProjectRay.utils.Utils;

import com.google.common.collect.Lists;

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
public enum ClickType {
	SUGGEST("SUGGEST"), EXECUTE("EXEC", "EXECUTE", "RUN"), LINK("LINK", "URL",
			"URI");

	private List<String> parsables = Lists.newArrayList();

	ClickType(String... names) {
		setParsables(Lists.newArrayList(names));
	}

	public List<String> getParsables() {
		return parsables;
	}

	public void setParsables(List<String> parsables) {
		this.parsables = parsables;
	}

	public String replace(String click) {
		String patternbase = "^[{%](%s)[}%]";
		for (String s : parsables) {
			Pattern p = Utils.compile(String.format(patternbase, s),
					Pattern.CASE_INSENSITIVE);
			if (p.matcher(click).find()) {
				return p.matcher(click).replaceFirst("");
			}
		}
		return click;
	}

	public static ClickType getType(String click) {
		String patternbase = "^[{%](%s)[}%]";
		for (ClickType type : values()) {
			for (String s : type.parsables) {
				Pattern p = Utils.compile(String.format(patternbase, s),
						Pattern.CASE_INSENSITIVE);
				if (p.matcher(click).find()) {
					return type;
				}
			}
		}
		return SUGGEST;
	}
}
