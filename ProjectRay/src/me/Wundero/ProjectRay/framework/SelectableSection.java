package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.variables.Parser;

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
public class SelectableSection extends Section {
	/*
	 * public SelectableSection(ConfigurationSection config) { super(config); if
	 * (!config.contains("display_no_permissions")) { setDisplay(false); } else
	 * { setDisplay(config.getBoolean("display_no_permissions")); }
	 * 
	 * if (!config.contains("no_permissions_text")) {
	 * this.setNoPermMessage(null); } else if (config.get("no_permissions_text")
	 * instanceof String) { this.setNoPermMessage(Lists.newArrayList(config
	 * .getString("no_permissions_text"))); } else {
	 * this.setNoPermMessage(Lists.newArrayList(config
	 * .getStringList("no_permissions_text"))); } if
	 * (!config.contains("prepend_no_permissions")) { setPrependNoPerm(false); }
	 * else { setPrependNoPerm(config.getBoolean("prepend_no_permissions")); } }
	 */

	public SelectableSection(String n, String t, String c, String p,
			List<String> h) {
		super(n, t, c, p, h);
	}

	@Override
	public FancyMessage getMessage(PlayerWrapper<?> player,
			PlayerWrapper<?>[] others) {
		if (!display) {
			return super.getMessage(player, others);
		}
		FancyMessage fm = super.getMessage(player, others);
		List<String> list = Lists.newArrayList();
		List<String> npm = Lists.newArrayList();
		if (super.getPermission() != null
		/* && !Hooks.has(player, "projectray." + super.getPermission()) */) {
			// TODO permisison check
			npm = Lists.newArrayList(noPermMessage);
		}
		if (prependNoPerm) {
			list.addAll(Parser.get().parseList(npm, player, others));
			if (super.getHover() != null && !super.getHover().isEmpty()) {
				list.addAll(Parser.get().parseList(super.getHover(), player,
						others));
			}
		} else {
			if (super.getHover() != null && !super.getHover().isEmpty()) {
				list.addAll(Parser.get().parseList(super.getHover(), player,
						others));
			}
			list.addAll(Parser.get().parseList(npm, player, others));
		}
		fm.tooltip(list);
		return fm;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public List<String> getNoPermMessage() {
		return noPermMessage;
	}

	public void setNoPermMessage(List<String> noPermMessage) {
		this.noPermMessage = noPermMessage;
	}

	public boolean isPrependNoPerm() {
		return prependNoPerm;
	}

	public void setPrependNoPerm(boolean prependNoPerm) {
		this.prependNoPerm = prependNoPerm;
	}

	private boolean display;
	private List<String> noPermMessage;
	private boolean prependNoPerm;
}
