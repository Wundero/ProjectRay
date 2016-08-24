package me.Wundero.ProjectRay;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.config.InternalClickAction;
import me.Wundero.ProjectRay.config.InternalHoverAction;
import me.Wundero.ProjectRay.framework.Groups;
import me.Wundero.ProjectRay.framework.RayPlayer;
import me.Wundero.ProjectRay.framework.channel.ChatChannels;
import me.Wundero.ProjectRay.framework.format.Format;
import me.Wundero.ProjectRay.framework.format.StaticFormat;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.Variables;
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
 * Singleton instance for all pseudo-singleton access classes. Keeps things in a
 * central place and is more secure.
 * 
 */
public class Ray {

	private static Ray singleton = new Ray();

	// registering async tasks so they can be cancelled on termination
	private List<Task> asyncTasks = Utils.sl();
	// tasks when formats load - shouldn't be necessary to have this but i need
	// it for some reason
	private List<Task> formatTasks = Utils.sl();

	public void registerTask(Task t) {
		if (t.isAsynchronous()) {
			asyncTasks.add(t);
		}
	}

	public boolean hasFormatTask() {
		return !formatTasks.isEmpty();
	}

	public void registerFormatTask(Task t) {
		registerTask(t);
		formatTasks.add(t);
	}

	public void finishFormatTask(Task t) {
		formatTasks.remove(t);
	}

	private Ray() {
	}

	public static Ray get() {
		return singleton;
	}

	// instance of the plugin to give to things like tasks and sponge singleton
	// calls
	private ProjectRay plugin;
	// config file
	private ConfigurationNode config;
	// groups that are loaded from file
	private Groups groups;
	// variable parser
	private Variables vars;
	// channels store
	private ChatChannels channels;
	// this was a test to see if i could properly load formats, but nope
	private boolean loadableSet = false;
	// all configs created/loaded by the plugin through Utils are saved here.
	private Map<ConfigurationLoader<CommentedConfigurationNode>, ConfigurationNode> toSave = Utils.sm();

	public void registerLoader(ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationNode node) {
		toSave.put(loader, node);
	}

	public void load(ProjectRay plugin) {
		this.setPlugin(plugin);
		this.setConfig(plugin.getConfig());
		loadableSet = config.getNode("loadable").getValue() != null;
		this.setVariables(new Variables());
		this.setChannels(new ChatChannels());
		try {
			File f = new File(plugin.getConfigDir().toFile(), "channels.conf");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} else if (f.isDirectory()) {
				f.delete();
				f.createNewFile();
			}
			channels.load(Utils.load(f));
		} catch (Exception e) {
			Utils.printError(e);
		}
	}

	public void setLoadable(UUID u) {
		if (!loadableSet) {
			try {
				config.getNode("loadable").setValue(TypeToken.of(UUID.class), u);
			} catch (ObjectMappingException e) {
				config.getNode("loadable").setValue(u);
			}
			loadableSet = true;
		}
	}

	public void setLoadable(User u) {
		setLoadable(u.getUniqueId());
	}

	public ProjectRay getPlugin() {
		return plugin;
	}

	public Logger getLogger() {
		return getPlugin().getLogger();
	}

	public void terminate() {// end async tasks and save configs and players
		for (Task t : asyncTasks) {
			t.cancel();
		}
		try {
			RayPlayer.saveAll();
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		for (ConfigurationLoader<?> loader : toSave.keySet()) {
			try {
				ConfigurationNode node = toSave.get(loader);
				loader.save(node);
			} catch (IOException e) {
				Utils.printError(e);
			}
		}
	}

	public Game getGame() {
		return plugin.getGame();
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

	// create map for args in a template to apply - is recursive if
	// useClickHover is true
	// TODO add support for vars in non-arg clickhover
	public Map<String, Object> setVars(Map<String, Object> known, TextTemplate template, Optional<Player> sender,
			Optional<Player> recip, Optional<Player> observer, Optional<Format> formatUsed, boolean useClickHover) {
		if (known == null) {
			known = Utils.sm();
		}
		// template is required to get the args to fill
		if (template == null) {
			if (formatUsed.isPresent() && formatUsed.get() instanceof StaticFormat) {
				TextTemplate t2 = ((StaticFormat) formatUsed.get()).getTemplate().orElse(null);
				if (t2 != null) {
					template = t2;
				} else {
					return known;
				}
			} else {
				return known;
			}
		}

		// for every key that is known that exists in the template, add it to
		// fill
		Map<String, Object> out = Utils.sm();
		for (String key : known.keySet()) {
			if (template.getArguments().containsKey(key)) {
				out.put(key, known.get(key));
			}
		}
		// if this is not null, click and hover will be applied to args.
		ConfigurationNode args = null;
		if (formatUsed.isPresent() && formatUsed.get().getNode().isPresent()
				&& formatUsed.get() instanceof StaticFormat) {
			args = formatUsed.get().getNode().get().getNode("format_args", "arguments");
		}
		// for the unfilled args
		if (template != null && template.getArguments() != null) {
			for (String key : template.getArguments().keySet()) {
				String k = key;
				// killer replaced with displayname killer for var parsing
				// purposes; not replaced in template
				if (k.equalsIgnoreCase("killer") && !out.containsKey(key)) {
					k = "displayname:killer";
				}
				boolean irecip = false;
				if (k.toLowerCase().startsWith("recip_")) {
					k = k.substring("recip_".length());
					irecip = true;
				}
				if (!out.containsKey(key)) {
					Optional<Player> s1;
					Optional<Player> r1;
					if (irecip) {// who is parsed as sender depends on recip tag
						s1 = recip;
						r1 = sender;
					} else {
						s1 = sender;
						r1 = recip;
					}
					// parse var into object
					Object var = getVariables().get(k, s1, r1, formatUsed, Optional.of(template), observer);
					Object var2 = var;
					if (args != null) {
						// apply var parsing to args
						Text t = var instanceof Text ? (Text) var : Text.of(var.toString());
						Text.Builder newVar = t.toBuilder();
						if (useClickHover) { // recursive/unsafe format failsafe
							try {
								InternalClickAction<?> click = args.getNode(key, "click")
										.getValue(TypeToken.of(InternalClickAction.class));
								InternalHoverAction<?> hover = args.getNode(key, "hover")
										.getValue(TypeToken.of(InternalHoverAction.class));
								// get values for args and apply if they are
								// there
								if (click != null) {
									if (click instanceof InternalClickAction.ATemplate) {
										((InternalClickAction.ATemplate) click)
												.apply(setVars(known, (TextTemplate) click.getResult(), sender, recip,
														observer, formatUsed, false));
									}
									click.applyTo(newVar);
								}
								if (hover != null) {
									if (hover instanceof InternalHoverAction.ShowTemplate) {
										((InternalHoverAction.ShowTemplate) hover)
												.apply(setVars(known, (TextTemplate) hover.getResult(), sender, recip,
														observer, formatUsed, false));
									}
									hover.applyTo(newVar);
								}
							} catch (Exception e) {
								Utils.printError(e);
							}
						}
						var2 = newVar.build();
					}
					out.put(key, var2);
				}
			}
		}
		return out;
	}

	public Variables getVariables() {
		return vars;
	}

	public void setVariables(Variables vars) {
		this.vars = vars;
	}

	public ChatChannels getChannels() {
		return channels;
	}

	public void setChannels(ChatChannels channels) {
		this.channels = channels;
	}
}
