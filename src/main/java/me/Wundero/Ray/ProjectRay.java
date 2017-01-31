package me.Wundero.Ray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import me.Wundero.Ray.commands.ClearChatCommand;
import me.Wundero.Ray.commands.Commands;
import me.Wundero.Ray.commands.IgnoreCommand;
import me.Wundero.Ray.commands.MessageCommand;
import me.Wundero.Ray.commands.MuteCommand;
import me.Wundero.Ray.commands.ReplyCommand;
import me.Wundero.Ray.commands.SpyCommand;
import me.Wundero.Ray.framework.Groups;
import me.Wundero.Ray.listeners.MainListener;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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

/**
 * Ray - A comprehensive and inclusive chat plugin. By Wundero.
 * 
 * This class is the main instance of the plugin, which stores much of Sponge's
 * plugin requisite information.
 */
@Plugin(id = ProjectRay.ID, name = ProjectRay.NAME, version = ProjectRay.VERSION, description = "A comprehensive and inclusive chat plugin.", authors = {
		"Wundero" })
public class ProjectRay {

	public static final String NAME = "Ray";
	public static final String ID = "ray";
	public static final String VERSION = "1.0.0";

	/**
	 * The logger used by the plugin.
	 */
	@Inject
	private Logger logger;
	/**
	 * The current sponge game instance.
	 */
	@Inject
	private Game game;
	/**
	 * The path to the config. Held in it's own folder.
	 */
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	/**
	 * The Config loader
	 */
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	/**
	 * The configuration itself, as loaded into memory.
	 */
	private ConfigurationNode config;

	/**
	 * Returns the logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Logs a string to the logger under the INFO level.
	 */
	public void log(String s) {
		getLogger().info(s);
	}

	/**
	 * Returns the game.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Returns the configuration folder
	 */
	public Path getConfigDir() {
		return configDir;
	}

	/**
	 * Return the file used for config.
	 */
	public Path getConfigPath() { // ensures config exists and returns path with
									// file info
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
		try {
			setConfig(loader.load());
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	private void saveConfig() {
		if (config == null) {
			return;
		}
		try {
			// This will allow safe editing out of game while still saving in
			// game changes (in game has priority) (theoretically) - not sure if
			// this works properly though
			ConfigurationNode toMerge = loader.load();
			ConfigurationNode node2 = config.mergeValuesFrom(toMerge);
			loader.save(node2);
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	/**
	 * Save the config.
	 */
	public synchronized void save() {
		saveConfig();
	}

	/**
	 * Fired when the game is initialized. Loads singleton values and registers
	 * important listeners.
	 * 
	 * @throws ObjectMappingException
	 */
	@Listener
	public void onStart(GameInitializationEvent event) throws ObjectMappingException {
		// registering listeners and loading singleton classes (safely-ish)
		loadConfig();
		Ray.get().load(this);
		Ray.get().setGroups(config.getValue(Groups.type));
	}

	/**
	 * Fired when the command /reload is fired.
	 * 
	 * @throws ObjectMappingException
	 */
	@Listener
	public void onReload(GameReloadEvent event) throws ObjectMappingException {
		game.getEventManager().unregisterPluginListeners(this);
		loadConfig();
		game.getEventManager().registerListeners(this, new MainListener());
		Ray.get().reload(this);
		Ray.get().setGroups(config.getValue(Groups.type));
	}

	/**
	 * Update singleton instance of economy for plugin reference.
	 */
	@Listener
	public void onEconUpdate(ChangeServiceProviderEvent event) {
		if (event.getService().equals(EconomyService.class)) {
			Ray.get().setEcon((EconomyService) event.getNewProviderRegistration().getProvider());
		}
	}

	/**
	 * Register all commands & listeners
	 */
	@Listener
	public void registerEvent(GameInitializationEvent event) {
		game.getEventManager().registerListeners(this, new MainListener());
		// register all commands. Might make this neater in the future.
		CommandSpec myCommandSpec = CommandSpec.builder().description(Text.of("Base command for Ray."))
				.children(Commands.getChildren()).executor(Commands.getExecutor()).permission("ray.use").build();
		game.getCommandManager().register(this, myCommandSpec, "ray", "projectray");
		game.getCommandManager().register(this,
				CommandSpec.builder().permission("ray.message").description(Text.of("Message a player."))
						.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
								GenericArguments.remainingJoinedStrings(Text.of("message")))
						.executor(new MessageCommand()).build(),
				"m", "msg", "message", "w", "whisper", "t", "tell");
		game.getCommandManager().register(this,
				CommandSpec.builder().permission("ray.message").description(Text.of("Reply to a player."))
						.arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
						.executor(new ReplyCommand()).build(),
				"r", "reply");
		game.getCommandManager().register(this,
				CommandSpec.builder().executor(new IgnoreCommand())
						.arguments(GenericArguments.player(Text.of("player"))).description(Text.of("Ignore a player."))
						.permission("ray.ignore").build(),
				"ignore");
		game.getCommandManager().register(this,
				CommandSpec.builder().executor(new ClearChatCommand()).description(Text.of("Clear the chat."))
						.permission("ray.clearchat")
						.arguments(
								GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("channel"))))
						.build(),
				"clearchat", "chatclear", "cc");
		game.getCommandManager().register(this,
				CommandSpec.builder().executor(new MuteCommand()).arguments(GenericArguments.player(Text.of("target")))
						.permission("ray.mute").description(Text.of("Mute a player")).build(),
				"mute");
		game.getCommandManager().register(this, CommandSpec.builder().executor(new SpyCommand()).permission("ray.spy")
				.description(Text.of("Toggle whether you can see private messages between other players.")).build(),
				"spy", "socialspy");
	}

	/**
	 * Stop all tasks, save the config, and clear singletons
	 */
	@Listener
	public void onStop(GameStoppingServerEvent event) {
		// run termination code
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
