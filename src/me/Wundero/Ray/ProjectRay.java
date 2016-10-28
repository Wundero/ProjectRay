package me.Wundero.Ray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import me.Wundero.Ray.commands.AfkCommand;
import me.Wundero.Ray.commands.ClearChatCommand;
import me.Wundero.Ray.commands.Commands;
import me.Wundero.Ray.commands.IgnoreCommand;
import me.Wundero.Ray.commands.MessageCommand;
import me.Wundero.Ray.commands.MuteCommand;
import me.Wundero.Ray.commands.QuoteCommand;
import me.Wundero.Ray.commands.ReplyCommand;
import me.Wundero.Ray.commands.channel.ChannelCommand;
import me.Wundero.Ray.commands.channel.ChannelHelpCommand;
import me.Wundero.Ray.commands.channel.ChannelJoinCommand;
import me.Wundero.Ray.commands.channel.ChannelLeaveCommand;
import me.Wundero.Ray.commands.channel.ChannelMuteCommand;
import me.Wundero.Ray.commands.channel.ChannelQMCommand;
import me.Wundero.Ray.commands.channel.ChannelRoleCommand;
import me.Wundero.Ray.commands.channel.ChannelSetupCommand;
import me.Wundero.Ray.commands.channel.ChannelWhoCommand;
import me.Wundero.Ray.config.InternalClickAction;
import me.Wundero.Ray.config.InternalHoverAction;
import me.Wundero.Ray.config.Template;
import me.Wundero.Ray.config.Templates;
import me.Wundero.Ray.framework.Groups;
import me.Wundero.Ray.framework.channel.ChannelMember;
import me.Wundero.Ray.framework.channel.ChannelMemberCollection;
import me.Wundero.Ray.framework.channel.ChatChannel;
import me.Wundero.Ray.listeners.ChatChannelListener;
import me.Wundero.Ray.listeners.MainListener;
import me.Wundero.Ray.utils.Utils;
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

/**
 * Ray - A comprehensive and inclusive chat plugin. By Wundero.
 * 
 * This class is the main instance of the plugin, which stores much of Sponge's
 * plugin requisite information.
 */
@Plugin(id = "ray", name = "Ray", version = "1.0.0", description = "A comprehensive and inclusive chat plugin.", authors = {
		"Wundero" })
public class ProjectRay {

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
	 * THe path to the config. Held in it's own folder.
	 */
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

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
	 * Get the version of the plugin.
	 */
	public String getVersion() {
		return "1.0.0";
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

	/**
	 * Register some of my serializers. I could have done this differently with
	 * a static method, but this allows me to apply the serializers to specific
	 * configuration loaders.
	 */
	public static ConfigurationOptions updateSerializers(ConfigurationOptions opts) {
		// adding custom serializers - makes config read/write easier
		TypeSerializerCollection t = opts.getSerializers();
		t.registerType(TypeToken.of(InternalClickAction.class), InternalClickAction.serializer());
		t.registerType(TypeToken.of(InternalHoverAction.class), InternalHoverAction.serializer());
		t.registerType(TypeToken.of(ChannelMember.class), ChannelMember.serializer());
		t.registerType(TypeToken.of(ChannelMemberCollection.class), ChannelMemberCollection.serializer());
		t.registerType(TypeToken.of(ChatChannel.class), ChatChannel.serializer());
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
		// if main info is missing, load a default template. Using advanced
		// template here, but in the future I will add more templates, and allow
		// for users to specify which to load either via cmd or config params
		if (config.getNode("worlds").isVirtual()) {
			Templates.TESTING(Template.builder(config));
			saveConfig();
		}
	}

	private void saveConfig() {
		if (config == null) {
			return;
		}
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
				.setPath(getConfigPath()).build();
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
	 */
	@Listener
	public void onStart(GameInitializationEvent event) {
		// registering listeners and loading singleton classes (safely-ish)
		loadConfig();
		Sponge.getEventManager().registerListeners(this, new MainListener());
		Ray.get().load(this);
		Ray.get().setGroups(new Groups(config.getNode("worlds")));
		Sponge.getEventManager().registerListeners(this, new ChatChannelListener());
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
	 * Register all commands
	 */
	@Listener
	public void registerCommandEvent(GameStartingServerEvent event) {
		// register all commands. Might make this neater in the future.
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
		Sponge.getCommandManager().register(this,
				CommandSpec.builder().executor(new IgnoreCommand())
						.arguments(GenericArguments.player(Text.of("player"))).description(Text.of("Ignore a player."))
						.permission("ray.ignore").build(),
				"ignore");
		Sponge.getCommandManager()
				.register(this,
						CommandSpec.builder().executor(new ClearChatCommand()).description(Text.of("Clear the chat."))
								.permission("ray.clearchat")
								.arguments(GenericArguments
										.optional(GenericArguments.remainingJoinedStrings(Text.of("clear"))))
								.build(),
						"clearchat", "chatclear", "cc");
		Sponge.getCommandManager()
				.register(this,
						CommandSpec.builder().executor(new QuoteCommand())
								.arguments(GenericArguments
										.optional(GenericArguments.remainingJoinedStrings(Text.of("quote"))))
								.permission("ray.quote").description(Text.of("Set your quote.")).build(),
						"quote");
		Sponge.getCommandManager().register(this, CommandSpec.builder().executor(new AfkCommand())
				.description(Text.of("Toggle being AFK.")).permission("ray.afk").build(), "afk");
		Sponge.getCommandManager().register(this,
				CommandSpec.builder().executor(new MuteCommand()).arguments(GenericArguments.player(Text.of("target")))
						.permission("ray.mute").description(Text.of("Mute a player")).build(),
				"mute");
		Sponge.getCommandManager()
				.register(this,
						CommandSpec.builder().permission("ray.channel").description(Text.of("Chat channels command."))
								.executor(
										new ChannelCommand())
								.child(CommandSpec.builder().executor(new ChannelQMCommand())
										.arguments(GenericArguments.string(Text.of("channel")),
												GenericArguments.remainingJoinedStrings(Text.of("message")))
										.description(Text.of("Message a channel without joining it."))
										.permission("ray.channel.quickmessage").build(), "quickmessage", "qm")
								.child(CommandSpec.builder().executor(new ChannelRoleCommand())
										.description(Text.of("View or set roles in a channel."))
										.permission("ray.channel.role")
										.arguments(
												GenericArguments.optional(GenericArguments.player(Text.of("target"))),
												GenericArguments.optional(GenericArguments
														.seq(GenericArguments.literal(Text.of("set"), "set"),
																GenericArguments
																		.remainingJoinedStrings(Text.of("role")))))
										.build(), "role", "r")
								.child(CommandSpec.builder().executor(new ChannelJoinCommand())
										.description(Text.of("Join a channel."))
										.arguments(GenericArguments.string(Text.of("channel"))).build(), "join", "j")
								.child(CommandSpec.builder().executor(new ChannelLeaveCommand())
										.description(Text.of("Leave a channel."))
										.arguments(
												GenericArguments.optional(GenericArguments.string(Text.of("channel"))))
										.build(), "leave", "l")
								.child(CommandSpec.builder().executor(new ChannelWhoCommand())
										.description(Text.of("List the players in a channel."))
										.arguments(
												GenericArguments.optional(GenericArguments.string(Text.of("channel"))))
										.build(), "who", "w")
								.child(CommandSpec.builder().executor(new ChannelMuteCommand())
										.arguments(GenericArguments.player(Text.of("target")),
												GenericArguments.optional(GenericArguments.string(Text.of("channel"))))
										.permission("ray.channel.mute")
										.description(
												Text.of("Mute a player. Optionally provide what channel to mute them in."))
										.build(), "mute")
								.child(CommandSpec.builder().executor(new ChannelHelpCommand())
										.description(Text.of("List channel commands.")).build(), "help", "h")
								.child(CommandSpec.builder().executor(new ChannelSetupCommand())
										.permission("ray.channel.setup")
										.arguments(GenericArguments
												.optional(GenericArguments.remainingJoinedStrings(Text.of("type"))))
										.build(), "setup")
								.build(),
						"channel", "ch");
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
