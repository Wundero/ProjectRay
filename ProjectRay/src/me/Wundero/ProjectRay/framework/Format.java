package me.Wundero.ProjectRay.framework;

import java.util.List;

import com.google.common.collect.Lists;

import ninja.leaping.configurate.ConfigurationNode;

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

//No world-specific formats, all specified under group
public class Format extends Section {

	// TODO default formats - create default package that handles default
	// grps/formats/everything else

	private List<Section> sections;
	private String name;
	private boolean animation = false; // if true, sections must specify what
										// frame they are part of with frame: 1
	private FormatType type;

	public Format(ConfigurationNode section) throws Exception {
		load(section);
	}

	// TODO getMessage

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public FormatType getType() {
		return type;
	}

	public void setType(FormatType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void load(ConfigurationNode section) {
		if (section == null) {
			return;
		}
		this.setName(section.getKey().toString());
		this.type = FormatType.fromString(name);
		this.sections = Lists.newArrayList();
		for (ConfigurationNode s : section.getChildrenList()) {
			if (!s.hasListChildren()) {
				continue;
			}
			Section se = Sections.createSection(s);
			sections.add(se);
		}
	}

	/**
	 * @return the animation
	 */
	public boolean isAnimation() {
		return animation;
	}

	/**
	 * @param animation
	 *            the animation to set
	 */
	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

}
