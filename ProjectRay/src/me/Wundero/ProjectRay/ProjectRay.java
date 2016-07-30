package me.Wundero.ProjectRay;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import me.Wundero.ProjectRay.commands.Commands;
import me.Wundero.ProjectRay.commands.MessageCommand;
import me.Wundero.ProjectRay.commands.ReplyCommand;
import me.Wundero.ProjectRay.config.InternalClickAction;
import me.Wundero.ProjectRay.config.InternalHoverAction;
import me.Wundero.ProjectRay.config.Template;
import me.Wundero.ProjectRay.config.Templates;
import me.Wundero.ProjectRay.framework.Groups;
import me.Wundero.ProjectRay.listeners.MainListener;
import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

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

@Plugin(id = "ray", name = "Ray", version = "1.0.0", description = "A comprehensive and inclusive chat plugin.", authors = {
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
			return "1.0.0";
		}
		return opt.get();
	}

	public Path getConfigDir() {
		return configDir;
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

	public static ConfigurationOptions updateSerializers(ConfigurationOptions opts) {
		TypeSerializerCollection t = opts.getSerializers();
		t.registerType(TypeToken.of(InternalClickAction.class), InternalClickAction.serializer());
		t.registerType(TypeToken.of(InternalHoverAction.class), InternalHoverAction.serializer());
		return opts.setSerializers(t);
	}

	private void loadConfig() {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
				.setPath(getConfigPath()).build();
		try {
			setConfig(loader.load(updateSerializers(loader.getDefaultOptions())));
		} catch (Exception e) {
			Utils.printError(e);
		}
		tryLoadDefaults();
	}

	private void tryLoadDefaults() {
		if (config.getNode("worlds").isVirtual()) {
			Templates.ADVANCED(Template.builder(config));
			saveConfig();
		}
	}

	private void saveConfig() {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
				.setPath(getConfigPath()).build();
		try {
			// This will allow safe editing out of game while still saving in
			// game changes (in game has priority)
			ConfigurationNode toMerge = loader.load();
			ConfigurationNode node2 = config.mergeValuesFrom(toMerge);
			loader.save(node2);
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	public synchronized void save() {
		saveConfig();
	}

	@Listener
	public void onStart(GameInitializationEvent event) {
		loadConfig();
		Sponge.getEventManager().registerListeners(this, new MainListener());
		Ray.get().load(this);
		Ray.get().setGroups(new Groups(config.getNode("worlds")));
	}

	@Listener
	public void registerCommandEvent(GameStartingServerEvent event) {
		CommandSpec myCommandSpec = CommandSpec.builder().description(Text.of("Base command for Ray."))
				.children(Commands.getChildren()).executor(Commands.getExecutor()).permission("ray.use").build();
		Sponge.getCommandManager().register(this, myCommandSpec, "ray", "projectray");
		Sponge.getCommandManager().register(this,
				CommandSpec.builder().permission("ray.message").description(Text.of("Message a player."))
						.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
								GenericArguments.remainingJoinedStrings(Text.of("message")))
						.executor(new MessageCommand()).build(),
				"m", "msg", "message", "w", "whisper", "t", "tell");
		Sponge.getCommandManager().register(this,
				CommandSpec.builder().permission("ray.message").description(Text.of("Reply to a player."))
						.arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
						.executor(new ReplyCommand()).build(),
				"r", "reply");
	}

	@Listener
	public void onStop(GameStoppingServerEvent event) {
		saveConfig();
		Ray.get().terminate();
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
