package me.Wundero.ProjectRay;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.Wundero.ProjectRay.config.Template;
import me.Wundero.ProjectRay.config.Templates;
import me.Wundero.ProjectRay.framework.Groups;
import me.Wundero.ProjectRay.listeners.MainListener;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

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

@Plugin(id = "ray", name = "Ray", version = "v0.0.1", description = "A comprehensive and inclusive chat plugin.", authors = {
		"Wundero" })
public class ProjectRay {

	@Inject
	private Logger logger;
	@Inject
	private Game game;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	private ConfigurationNode config;

	public Logger getLogger() {
		return logger;
	}

	public void log(String s) {
		getLogger().info(s);
	}

	public Game getGame() {
		return game;
	}

	public String getVersion() {
		Optional<String> opt = game.getPluginManager().getPlugin("ray").get().getVersion();
		if (!opt.isPresent()) {
			return "v0.0.1";
		}
		return opt.get();
	}

	public Path getConfigPath() {
		File f = new File(configDir.toFile(), "ray.conf");
		if (!configDir.toFile().exists()) {
			configDir.toFile().mkdirs();
		}
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				Utils.printError(e);
			}
		}
		return f.toPath();
	}

	private void loadConfig() {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
				.setPath(getConfigPath()).build();
		try {
			setConfig(loader.load());
		} catch (Exception e) {
			Utils.printError(e);
		}
		tryLoadDefaults();
	}

	private void tryLoadDefaults() {
		if (config.getNode("worlds").isVirtual()) {
			Templates.DEFAULT(Template.builder(config));
			saveConfig();
		}
	}

	private void saveConfig() {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
				.setPath(getConfigPath()).build();
		try {
			loader.save(config);
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	@Listener
	public void onStart(GameStartedServerEvent event) {
		loadConfig();
		Sponge.getEventManager().registerListeners(this, new MainListener());
		Ray.get().load(this);
		File f = new File(configDir.toFile(), "worlds");
		if (!f.exists() || !f.isDirectory()) {
			Ray.get().setGroups(new Groups(config.getNode("worlds")));
		} else {
			Ray.get().setGroups(new Groups(f));
		}
	}

	@Listener
	public void onStop(GameStoppingServerEvent event) {
		saveConfig();
	}

	/**
	 * @return the config
	 */
	public ConfigurationNode getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

}
