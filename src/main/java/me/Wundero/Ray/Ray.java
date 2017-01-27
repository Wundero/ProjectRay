package me.Wundero.Ray;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.Ray.framework.Groups;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.framework.player.RayPlayer;
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
	private PaginationService paginationService;

	// instance of the plugin to give to things like tasks and sponge singleton
	// calls
	private ProjectRay plugin;
	// config file
	private ConfigurationNode config;
	// groups that are loaded from file
	private Groups groups;
	// variable parser
	private Variables vars;
	// all configs created/loaded by the plugin through Utils are saved here.
	private Map<ConfigurationLoader<CommentedConfigurationNode>, ConfigurationNode> toSave = Utils.sm();
	// econ
	private EconomyService econ;
	// cache
	private UserCache cache;

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
			this.setEcon(Sponge.getServiceManager().provide(EconomyService.class)
					.orElseThrow(() -> new NullPointerException("Escape")));
		} catch (Exception e) {
			this.setEcon(null);
		}
	}

	/**
	 * Load singleton instances from the main plugin class
	 */
	public void load(ProjectRay plugin) {
		// instantiates all singletons contained within this class
		this.setPlugin(plugin);
		this.setConfig(plugin.getConfig());
		this.setVariables(new Variables());
		this.setCache(new UserCache());
		this.setPaginationService(plugin.getGame().getServiceManager().provideUnchecked(PaginationService.class));
		try {
			this.setEcon(Sponge.getServiceManager().provide(EconomyService.class)
					.orElseThrow(() -> new NullPointerException("Escape")));
		} catch (Exception e) {
			this.setEcon(null);
		}
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
		return applyVars(t, data, Optional.empty(), false);
	}

	/**
	 * Parse a template for variables and return the template's applied text
	 * vars
	 */
	public Text applyVars(TextTemplate t, ParsableData data, Optional<Format> format, boolean perms) {
		Map<String, Object> v = setVars(data, t, format);
		for (String a : t.getArguments().keySet()) {
			if (!v.containsKey(a)) {
				v.put(a, "");
			}
		}
		Text tx = t.apply(v).build();
		return TextUtils.vars(tx, data, perms);
	}

	/**
	 * Return a map with the String-Object map that can be applied to the given
	 * template
	 */
	public Map<String, Object> setVars(ParsableData data, TextTemplate template) {
		return setVars(data, template, Optional.empty());
	}

	/**
	 * Return a map with the String-Object map that can be applied to the given
	 * template
	 */
	public Map<String, Object> setVars(ParsableData data, TextTemplate template, Optional<Format> formatUsed) {
		Map<String, Object> known = data.getKnown().orElse(Utils.hm());
		// template is required to get the args to fill
		if (template == null) {
			return known;
		}

		// for every key that is known that exists in the template, add it to
		// fill
		Map<String, Object> out = Utils.hm();
		for (String key : known.keySet()) {
			if (template.getArguments().containsKey(key)) {
				out.put(key, known.get(key));
			}
		}
		// for the unfilled args
		if (template != null && template.getArguments() != null) {
			for (String key : template.getArguments().keySet()) {
				if (!out.containsKey(key)) {
					// parse
					Text var = getVariables().get(key, data, formatUsed, Optional.of(template));
					out.put(key, var);
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
	public PaginationService getPaginationService() {
		return paginationService;
	}

	/**
	 * @param paginationService
	 *            the paginationService to set
	 */
	public void setPaginationService(PaginationService paginationService) {
		this.paginationService = paginationService;
	}

}
