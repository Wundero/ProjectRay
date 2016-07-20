package me.Wundero.ProjectRay;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.framework.Groups;
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

public class Ray {

	private static Ray singleton = new Ray();

	private List<Task> asyncTasks = Lists.newArrayList();

	public void registerTask(Task t) {
		if (t.isAsynchronous()) {
			asyncTasks.add(t);
		}
	}

	private Ray() {
	}

	public static Ray get() {
		return singleton;
	}

	private ProjectRay plugin;
	private ConfigurationNode config;
	private Groups groups;
	private Variables vars;

	public void load(ProjectRay plugin) {
		this.setPlugin(plugin);
		this.setConfig(plugin.getConfig());
		this.setVariables(new Variables());
	}

	public void terminate() {
		for (Task t : asyncTasks) {
			t.cancel();
		}
	}

	public Game getGame() {
		return plugin.getGame();
	}

	public ProjectRay getPlugin() {
		return plugin;
	}

	public Logger getLogger() {
		return getPlugin().getLogger();
	}

	private void setPlugin(ProjectRay plugin) {
		this.plugin = plugin;
	}

	public ConfigurationNode getConfig() {
		return config;
	}

	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

	public Groups getGroups() {
		return groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	public Map<String, Object> setVars(Map<String, Object> known, TextTemplate template, Player sender,
			boolean isRecip) {
		Map<String, Object> out = Maps.newHashMap();
		for (String key : known.keySet()) {
			if (template.getArguments().containsKey(key)) {
				out.put(key, known.get(key));
			}
		}
		for (String key : template.getArguments().keySet()) {
			String k = key;
			if (isRecip) {
				k = "recip_" + key;
			}
			if (!out.containsKey(k)) {
				out.put(k, getVariables().get(key, sender));
			}
		}
		return out;// stub
	}

	public Variables getVariables() {
		return vars;
	}

	public void setVariables(Variables vars) {
		this.vars = vars;
	}
}
