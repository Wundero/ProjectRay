package me.Wundero.Ray.framework.player.name;
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

import java.util.function.Function;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.text.Text;

public class InstantDisplayName extends DisplayName {

	private Function<Text, Boolean> applier;
	private String separator;
	private boolean apply = true;

	/**
	 * Create a displayname object that updates upon passing a displayname.
	 */
	public InstantDisplayName(Text original, Function<Text, Boolean> applicant, String separator) {
		super(original);
		this.setApplier(Validate.notNull(applicant));
		this.setSeparator(separator);
	}

	/**
	 * Add a nickname and, if possible, apply it.
	 */
	@Override
	public boolean nickname(Text nickname) {
		boolean r = super.nickname(nickname);
		if (apply && r) {
			r = r && apply(applier, separator);
		}
		return r;
	}

	/**
	 * Add a suffix and, if possible, apply it.
	 */
	@Override
	public boolean suffix(Text suffix) {
		boolean r = super.suffix(suffix);
		if (apply && r) {
			r = r && apply(applier, separator);
		}
		return r;
	}

	/**
	 * Add a prefix and, if possible, apply it.
	 */
	@Override
	public boolean prefix(Text prefix) {
		boolean r = super.prefix(prefix);
		if (apply && r) {
			r = r && apply(applier, separator);
		}
		return r;
	}

	/**
	 * Add a displayname and, if possible, apply it.
	 */
	@Override
	public boolean offer(Text prefix, Text nickname, Text suffix) {
		apply = false;
		boolean ret = super.offer(prefix, nickname, suffix);
		if (ret == false) {
			apply = true;
			return false;
		}
		ret = ret && apply(applier, separator);
		apply = true;
		return ret;
	}

	/**
	 * @return the applier
	 */
	public Function<Text, Boolean> getApplier() {
		return applier;
	}

	/**
	 * @param applier
	 *            the applier to set
	 */
	public void setApplier(Function<Text, Boolean> applier) {
		this.applier = applier;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
