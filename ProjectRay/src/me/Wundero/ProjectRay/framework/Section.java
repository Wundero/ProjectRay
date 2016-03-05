package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.Parser;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;

public class Section {

	private String name;
	private String text;
	private String click;
	private String permission;
	private List<String> hover;

	public Section(String n, String t, String c, String p, List<String> h) {
		this.setName(n);
		this.setText(t);
		this.setClick(c);
		this.setPermission(p);
		this.setHover(Lists.newArrayList(h));
	}

	public Section(ConfigurationSection config) {
		if (config == null) {
			Utils.printError(new Exception("Config section cannot be null!"));
		}
		if (!Utils.validateConfigSections(config, "text")) {
			Utils.printError(new Exception(
					"Missing configuation elements for section "
							+ config.getName()));
		}
		this.setName(config.getName());
		this.setPermission(config.getString("permission"));
		this.setClick(config.getString("click"));
		this.setText(config.getString("text"));
		if (!config.contains("hover")) {
			this.setHover(null);
		} else if (config.get("hover") instanceof String) {
			this.setHover(Lists.newArrayList(config.getString("hover")));
		} else {
			this.setHover(Lists.newArrayList(config.getStringList("hover")));
		}
	}

	public static Section createSection(ConfigurationSection section) {
		if (section.getBoolean("selectable", false)) {
			return new SelectableSection(section);
		}
		if (section.getBoolean("cacheable", false)) {
			return new CacheableSection(section);
		}
		return new Section(section);
	}

	public List<String> getHover() {
		return hover;
	}

	public void setHover(List<String> hover) {
		this.hover = hover;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FancyMessage getMessage(OfflinePlayer player, OfflinePlayer[] others) {
		if (permission != null) {
			if (!Hooks.has(player, "projectray." + permission)) {
				return Utils.F();
			}
		}
		FancyMessage fm = new FancyMessage();
		String t = Parser.get().parse(player, others, text);
		String c = null;
		if (click != null) {
			c = Parser.get().parse(player, others, click);// TODO get click type
		}
		List<String> h = null;
		if (hover != null && !hover.isEmpty()) {
			h = Parser.get().parseList(hover, player, others);
		}
		fm.text(t);
		if (h != null) {
			fm.tooltip(h);
		}
		if (c != null) {
			fm.command(c);
		}
		return fm;
	}
}
