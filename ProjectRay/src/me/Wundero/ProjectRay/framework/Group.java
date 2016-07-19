package me.Wundero.ProjectRay.framework;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

public class Group {
	private Map<FormatType, List<Format>> formats = Maps.newHashMap();
	private List<String> parents = Lists.newArrayList();
	private String world;
	private int priority;
	private String permission;
	private ConfigurationNode config;
	private String name;
	private boolean isDir = false;
	private boolean global = false;
	private Path dir;

	// TODO default group

	public Group(String world, ConfigurationNode config, Path directory, boolean global) {
		this.setGlobal(global);
		this.setWorld(world);
		this.setConfig(config);
		isDir = !(directory == null);
		this.setDir(directory);
		load();
	}

	public synchronized void load() {
		this.setName(config.getKey().toString());
		this.setPermission(config.getNode("permission").getString("ray." + getName()));
		this.setPriority(config.getNode("priority").getInt(0));
		try {
			this.setParents(config.getNode("parents").getList(TypeToken.of(String.class)));
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		loadFormats();
	}

	private synchronized void lf(ConfigurationNode nod) {
		ConfigurationNode nod2;
		if (nod.getNode("formats").isVirtual()) {
			nod2 = nod;
		} else {
			nod2 = nod.getNode("formats");
		}
		for (ConfigurationNode node : nod2.getChildrenList()) {
			Format f = new Format(node);
			FormatType type = f.getType();
			if (!formats.containsKey(type)) {
				formats.put(type, Lists.newArrayList(f));
			} else {
				List<Format> list = formats.get(type);
				list.add(f);
				formats.put(type, list);
			}
		}
	}

	private synchronized void loadFormats() {
		if (!isDir) {
			lf(config);
		} else {
			File fdir = new File(dir.toFile(), "formats");
			if (!fdir.exists()) {
				fdir.mkdirs();
			}
			if (fdir.isFile()) {
				ConfigurationNode conf = Utils.load(fdir);
				lf(conf);
			} else {
				for (File fil : fdir.listFiles()) {
					Format f = new Format(Utils.load(fil));
					FormatType type = f.getType();
					if (!formats.containsKey(type)) {
						formats.put(type, Lists.newArrayList(f));
					} else {
						List<Format> list = formats.get(type);
						list.add(f);
						formats.put(type, list);
					}
				}
			}
		}
	}

	public Format getFormat(FormatType type, int index) {
		if (getFormats(type) == null || getFormats(type).isEmpty()) {
			return null;
		}
		return getFormats(type).get(index);
	}

	public Format getFormat(FormatType type) {
		return getFormat(type, 0);
	}

	public Format getFormat(FormatType type, boolean random) {
		if (random) {
			return getRandomFormat(type);
		}
		return getFormat(type, 0);
	}

	public Format getRandomFormat(FormatType type) {
		List<Format> fmats = getFormats(type);
		return fmats.get(new Random().nextInt(fmats.size()));
	}

	public List<Format> getAllFormats() {
		List<Format> out = Lists.newArrayList();
		for (List<Format> f : formats.values()) {
			out.addAll(f);
		}
		return out;
	}

	public List<Format> getFormats(FormatType type) {
		return formats.get(type);
	}

	public ConfigurationNode getConfig() {
		return config;
	}

	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

	public List<String> getParents() {
		return parents;
	}

	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Path getDir() {
		return dir;
	}

	public void setDir(Path dir) {
		this.dir = dir;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}
}
