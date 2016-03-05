package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.variables.Parser;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;

public class SelectableSection extends Section {
	public SelectableSection(ConfigurationSection config) {
		super(config);
		if (!config.contains("display_no_permissions")) {
			setDisplay(false);
		} else {
			setDisplay(config.getBoolean("display_no_permissions"));
		}

		if (!config.contains("no_permissions_text")) {
			this.setNoPermMessage(null);
		} else if (config.get("no_permissions_text") instanceof String) {
			this.setNoPermMessage(Lists.newArrayList(config
					.getString("no_permissions_text")));
		} else {
			this.setNoPermMessage(Lists.newArrayList(config
					.getStringList("no_permissions_text")));
		}
		if (!config.contains("prepend_no_permissions")) {
			setPrependNoPerm(false);
		} else {
			setPrependNoPerm(config.getBoolean("prepend_no_permissions"));
		}
	}

	@Override
	public FancyMessage getMessage(OfflinePlayer player, OfflinePlayer[] others) {
		if (!display) {
			return super.getMessage(player, others);
		}
		FancyMessage fm = super.getMessage(player, others);
		List<String> list = Lists.newArrayList();
		List<String> npm = Lists.newArrayList();
		if (super.getPermission() != null
				&& !Hooks.has(player, "projectray." + super.getPermission())) {
			npm = Lists.newArrayList(noPermMessage);
		}
		if (prependNoPerm) {
			list.addAll(Parser.get().parseList(npm, player, others));
			if (super.getHover() != null && !super.getHover().isEmpty()) {
				list.addAll(Parser.get().parseList(super.getHover(), player,
						others));
			}
		} else {
			if (super.getHover() != null && !super.getHover().isEmpty()) {
				list.addAll(Parser.get().parseList(super.getHover(), player,
						others));
			}
			list.addAll(Parser.get().parseList(npm, player, others));
		}
		fm.tooltip(list);
		return fm;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public List<String> getNoPermMessage() {
		return noPermMessage;
	}

	public void setNoPermMessage(List<String> noPermMessage) {
		this.noPermMessage = noPermMessage;
	}

	public boolean isPrependNoPerm() {
		return prependNoPerm;
	}

	public void setPrependNoPerm(boolean prependNoPerm) {
		this.prependNoPerm = prependNoPerm;
	}

	private boolean display;
	private List<String> noPermMessage;
	private boolean prependNoPerm;
}
