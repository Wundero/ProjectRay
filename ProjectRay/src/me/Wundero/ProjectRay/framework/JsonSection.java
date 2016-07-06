package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.framework.config.ConfigSection;
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
public class JsonSection extends Section {

	private String name;
	private String text;
	private String click;
	private String permission;
	private List<String> hover;

	public JsonSection(ConfigSection config) {
		load(config);
	}

	public List<String> getHover() {
		return hover;
	}

	public JsonSection setHover(List<String> hover) {
		this.hover = hover;
		return this;
	}

	public String getPermission() {
		return permission;
	}

	public JsonSection setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	public String getClick() {
		return click;
	}

	public JsonSection setClick(String click) {
		this.click = click;
		return this;
	}

	public String getText() {
		return text;
	}

	public JsonSection setText(String text) {
		this.text = text;
		return this;
	}

	public String getName() {
		return name;
	}

	public JsonSection setName(String name) {
		this.name = name;
		return this;
	}

	// TODO getMessage

	@Override
	public void load(ConfigSection config) {
		if (config == null) {
			Utils.printError(new Exception("Config section cannot be null!"));
			return;
		}
		if (!Utils.validateConfigSections(config, "text")) {
			Utils.printError(new Exception("Missing configuation elements for section " + config.getName()));
			return;
		}
		this.setName(config.getName());
		this.setPermission(config.getString("permission"));
		this.setClick(config.getString("click"));
		this.setText(config.getString("text"));
		if (!config.contains("hover")) {
			this.setHover(null);
		} else if (config.get("hover") instanceof String) {
			this.setHover(Lists.newArrayList(config.getString("hover")));
		} else {
			this.setHover(Lists.newArrayList(config.getStringList("hover")));
		}
	}
}
