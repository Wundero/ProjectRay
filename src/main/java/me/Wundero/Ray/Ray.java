package me.Wundero.Ray;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.config.InternalClickAction;
import me.Wundero.Ray.config.InternalHoverAction;
import me.Wundero.Ray.framework.Groups;
import me.Wundero.Ray.framework.channel.ChatChannels;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.format.StaticFormat;
import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.listeners.AfkListener;
import me.Wundero.Ray.pagination.RayPaginationService;
import me.Wundero.Ray.tag.TagStore;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.UserCache;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import me.Wundero.Ray.variables.Variables;
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

	/**
	 * Register an asynchronous task to the list - cancels on stop.
	 */
	public void registerTask(Task t) {
		if (t.isAsynchronous()) {
			asyncTasks.add(t);
		}
	}

	private Ray() {
	}

	/**
	 * Return the Ray singleton
	 */
	public static Ray get() {
		return singleton;
	}

	// paginate texts. Use instead of sponges pagination service.
	private RayPaginationService paginationService;

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
	// all configs created/loaded by the plugin through Utils are saved here.
	private Map<ConfigurationLoader<CommentedConfigurationNode>, ConfigurationNode> toSave = Utils.sm();
	// tag store
	private TagStore tags;
	// econ
	private EconomyService econ;
	// cache
	private UserCache cache;

	private boolean useChatMenus = false;

	/**
	 * Return the economy service, if available
	 */
	public Optional<EconomyService> getEcon() {
		return Utils.wrap(econ);
	}

	/**
	 * Update economy service.
	 */
	public void setEcon(EconomyService econ) {
		this.econ = econ;
	}

	/**
	 * Register configuration node - loader map to save on termination - any
	 * configs loaded should do this
	 */
	public void registerLoader(ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationNode node) {
		toSave.put(loader, node);
	}

	/**
	 * Reload singleton instances.
	 */
	public void reload(ProjectRay plugin) {
		this.setConfig(plugin.getConfig());
		this.getGroups().terminate();
		this.setGroups(null);
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
		try {
			File f = new File(plugin.getConfigDir().toFile(), "tags.conf");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} else if (f.isDirectory()) {
				f.delete();
				f.createNewFile();
			}
			tags.load(Utils.load(f));
		} catch (Exception e) {
			Utils.printError(e);
		}
		if (channels.useChannels()) {
			this.setUseChatMenus(config.getNode("channelmenus", "enabled").getBoolean(false));
		} else {
			this.setUseChatMenus(false);
		}
		try {
			this.setEcon(Sponge.getServiceManager().provide(EconomyService.class)
					.orElseThrow(() -> new NullPointerException("Escape")));
		} catch (Exception e) {
			this.setEcon(null);
		}
		new AfkListener(config.getNode("afk", "timer").getInt(-1), config.getNode("afk", "kick").getInt(-1),
				config.getNode("afk", "cancel-movement").getBoolean(false));
	}

	/**
	 * Load singleton instances from the main plugin class
	 */
	public void load(ProjectRay plugin) {
		// instantiates all singletons contained within this class
		this.setPlugin(plugin);
		this.setConfig(plugin.getConfig());
		this.setVariables(new Variables());
		this.setChannels(new ChatChannels());
		this.setTags(new TagStore());
		this.setCache(new UserCache());
		this.setPaginationService(new RayPaginationService());
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
		try {
			File f = new File(plugin.getConfigDir().toFile(), "tags.conf");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} else if (f.isDirectory()) {
				f.delete();
				f.createNewFile();
			}
			tags.load(Utils.load(f));
		} catch (Exception e) {
			Utils.printError(e);
		}
		if (channels.useChannels()) {
			this.setUseChatMenus(config.getNode("channelmenus", "enabled").getBoolean(false));
		} else {
			this.setUseChatMenus(false);
		}
		try {
			this.setEcon(Sponge.getServiceManager().provide(EconomyService.class)
					.orElseThrow(() -> new NullPointerException("Escape")));
		} catch (Exception e) {
			this.setEcon(null);
		}
		new AfkListener(config.getNode("afk", "timer").getInt(-1), config.getNode("afk", "kick").getInt(-1),
				config.getNode("afk", "cancel-movement").getBoolean(false));
	}

	/**
	 * Get main plugin object
	 */
	public ProjectRay getPlugin() {
		return plugin;
	}

	/**
	 * Get plugin logger
	 */
	public Logger getLogger() {
		return getPlugin().getLogger();
	}

	/**
	 * Save configs and cancel async tasks
	 */
	public void terminate() {// end async tasks and save configs and players
		for (Task t : asyncTasks) {
			t.cancel();
		}
		try {
			RayPlayer.saveAll();
		} catch (ObjectMappingException e) {
			Utils.printError(e);
		}
		try {
			this.getChannels().save();
		} catch (Exception e) {
		}
		try {
			this.getTags().save();
		} catch (ObjectMappingException e1) {
			Utils.printError(e1);
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

	/**
	 * Return the current game instance
	 */
	public Game getGame() {
		return plugin.getGame();
	}

	private void setPlugin(ProjectRay plugin) {
		this.plugin = plugin;
	}

	/**
	 * Return the plugin's main config file
	 */
	public ConfigurationNode getConfig() {
		return config;
	}

	/**
	 * Update singleton config file
	 */
	public void setConfig(ConfigurationNode config) {
		this.config = config;
	}

	/**
	 * Get Groups singleton
	 */
	public Groups getGroups() {
		return groups;
	}

	/**
	 * Update Groups singleton
	 */
	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	/**
	 * Parse a template for variables and return the template's applied text
	 * vars
	 */
	public Text applyVars(TextTemplate t, ParsableData data) {
		return applyVars(t, data, Optional.empty(), true);
	}

	/**
	 * Parse a template for variables and return the template's applied text
	 * vars
	 */
	public Text applyVars(TextTemplate t, ParsableData data, Optional<Format> format, boolean clickhover) {
		Map<String, Object> v = setVars(data, t, format, clickhover);
		for (String a : t.getArguments().keySet()) {
			if (!v.containsKey(a)) {
				v.put(a, "");
			}
		}
		Text tx = t.apply(v).build();
		return TextUtils.vars(tx, data);
	}

	/**
	 * Return a map with the String-Object map that can be applied to the given
	 * template
	 */
	public Map<String, Object> setVars(ParsableData data, TextTemplate template) {
		return setVars(data, template, Optional.empty(), true);
	}

	/**
	 * Return a map with the String-Object map that can be applied to the given
	 * template
	 */
	public Map<String, Object> setVars(ParsableData data, TextTemplate template, Optional<Format> formatUsed,
			boolean ch) {
		return setVars(data.getKnown().orElse(Utils.hm()), template, data.getSender(), data.getRecipient(),
				data.getObserver(), formatUsed, ch);
	}

	/**
	 * Return a map with the String-Object map that can be applied to the given
	 * template
	 */
	public Map<String, Object> setVars(Map<String, Object> known, TextTemplate template, Optional<Player> sender,
			Optional<Player> recip, Optional<Player> observer, Optional<Format> formatUsed, boolean useClickHover) {
		if (known == null) {
			known = Utils.hm();
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
		Map<String, Object> out = Utils.hm();
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
					Object var = getVariables().get(k,
							new ParsableData().withSender(s1).withRecipient(r1).withObserver(observer), formatUsed,
							Optional.of(template));// probably could replace
													// this with just Text but
													// meh
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

	/**
	 * Get variable parser singleton
	 */
	public Variables getVariables() {
		return vars;
	}

	/**
	 * Set variable parser singleton
	 */
	public void setVariables(Variables vars) {
		this.vars = vars;
	}

	/**
	 * Get chat channels singleton
	 */
	public ChatChannels getChannels() {
		return channels;
	}

	/**
	 * Set chat channels singelton
	 */
	public void setChannels(ChatChannels channels) {
		this.channels = channels;
	}

	/**
	 * @return the tags
	 */
	public TagStore getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(TagStore tags) {
		this.tags = tags;
	}

	/**
	 * @return the cache
	 */
	public UserCache getCache() {
		return cache;
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(UserCache cache) {
		this.cache = cache;
	}

	/**
	 * @return the paginationService
	 */
	public RayPaginationService getPaginationService() {
		return paginationService;
	}

	/**
	 * @param paginationService
	 *            the paginationService to set
	 */
	public void setPaginationService(RayPaginationService paginationService) {
		this.paginationService = paginationService;
	}

	/**
	 * @return whether to use chat menus
	 */
	public boolean isUseChatMenus() {
		return useChatMenus;
	}

	/**
	 * @param useChatMenus
	 *            set whether to use chat menus
	 */
	private void setUseChatMenus(boolean useChatMenus) {
		this.useChatMenus = useChatMenus;
	}
}
