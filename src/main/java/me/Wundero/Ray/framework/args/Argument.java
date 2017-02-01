package me.Wundero.Ray.framework.args;
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

import me.Wundero.Ray.config.Rootable;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Argument implements Rootable {

	private String key;
	@Setting
	private Text value;
	@Setting
	private boolean override = false;

	public Argument() {
	}

	public Argument(String k, Text v) {
		this.key = k;
		this.value = v;
	}

	public void setValue(Text text) {
		this.value = text;
	}

	public void setOverride(boolean o) {
		this.override = o;
	}

	/**
	 * @return whether to override the string in the text.
	 */
	public boolean override() {
		return override;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value but with variables parsed
	 */
	public Text getValue(ParsableData data) {
		return TextUtils.vars(value, data, true);
	}

	/**
	 * @return the value
	 */
	public Text getValue() {
		return value;
	}

	@Override
	public void applyRoot(String name, ConfigurationNode root) {
		this.key = name;
	}
}
