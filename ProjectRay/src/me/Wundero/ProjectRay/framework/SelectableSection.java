package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;

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
		if (prependNoPerm) {
			list.addAll(noPermMessage);
			list.addAll(super.getHover());
		} else {
			list.addAll(super.getHover());
			list.addAll(noPermMessage);
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
