package me.Wundero.ProjectRay.framework;

import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;

public class Format {

	// TODO default formats
	// TODO figoure out formats on worlds/servers/bungees/whatever type of thing

	private List<Section> sections;
	private String name;
	private FormatType type;

	public Format(ConfigurationSection section) throws Exception {
		if (section == null) {
			throw new Exception("Configuration Section cannot be null!");
		}
		this.setName(section.getName());
		this.type = FormatType.fromString(name);
		this.sections = Lists.newArrayList();
		for (String s : section.getKeys(false)) {
			if (!(section.get(s) instanceof ConfigurationSection)) {
				continue;
			}
			ConfigurationSection sect = section.getConfigurationSection(s);
			sections.add(Section.createSection(sect));
		}
	}

	public FancyMessage getMessage(OfflinePlayer player, OfflinePlayer[] others) {
		FancyMessage out = new FancyMessage();
		out.getList().clear();
		for (Section s : sections) {
			out.getList().addAll(s.getMessage(player, others).getList());
		}
		return out;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public FormatType getType() {
		return type;
	}

	public void setType(FormatType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
