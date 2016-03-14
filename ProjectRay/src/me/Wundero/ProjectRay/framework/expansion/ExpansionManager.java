package me.Wundero.ProjectRay.framework.expansion;

import java.util.HashMap;
import java.util.Map;

import me.Wundero.ProjectRay.ProjectRay;
import me.Wundero.ProjectRay.framework.iface.Configurable;
import me.Wundero.ProjectRay.framework.iface.Taskable;
import me.Wundero.ProjectRay.variables.Store;
import me.Wundero.ProjectRay.variables.Variable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Maps;

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
public class ExpansionManager {

	private final HashMap<String, Expansion> registeredExpansions = Maps
			.newHashMap();

	public ExpansionManager() {
	}

	public boolean registerExpansion(Expansion e) {
		if (e.identifier() == null) {
			return false;
		}
		if (e.plugin() == null
				|| !Bukkit.getPluginManager().isPluginEnabled(e.plugin())) {
			return false;
		}
		if (registeredExpansions.containsKey(e.plugin())) {
			return false;
		}
		if (!e.register()) {
			return false;
		}
		registeredExpansions.put(e.plugin(), e);
		if (e instanceof Variable) {
			Store.get().add((Variable) e);
		}
		// TODO if is json event how 2 handle
		if (e instanceof Configurable) {
			Map<String, Object> defs = ((Configurable) e).getDefaults();
			String section = "expansions." + e.identifier();
			ConfigurationSection sect = ProjectRay.get().getConfig();
			if (!sect.contains(section)) {
				sect = sect.createSection(section);
			} else {
				sect = sect.getConfigurationSection(section);
			}
			boolean sa = false;
			for (String s : defs.keySet()) {
				if (s == null || s.isEmpty()) {
					continue;
				}
				if (defs.get(s) == null) {
					sa = true;
					sect.set(s, null);
				} else if (!sect.contains(s)) {
					sect.set(s, defs.get(s));
					sa = true;
				}
			}
			if (sa) {
				ProjectRay.get().saveConfig();
				ProjectRay.get().reloadConfig();
			}
			((Configurable) e).load(sect.getValues(true));
		}
		if (e instanceof Taskable) {
			((Taskable) e).start();
		}
		return true;
	}

}
