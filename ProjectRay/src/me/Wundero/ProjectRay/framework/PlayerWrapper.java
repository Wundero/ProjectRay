package me.Wundero.ProjectRay.framework;

import java.util.List;
import java.util.UUID;

public abstract class PlayerWrapper<T> {

	private T player;

	public PlayerWrapper(T player) {
		load(player);
	}

	public abstract UUID getUUID();

	public UUID getUniqueId() {
		return getUUID();
	}

	public abstract void chat(String text);

	public abstract void command(String command);

	public abstract void setUUID(UUID uuid);

	public abstract void load(T player);

	public abstract String getLastName();

	public abstract void setLastName(String lastName);

	public abstract List<String> getPermissions();

	public abstract List<Group> getGroups();

	public abstract boolean hasPermission(String permission);

	public T getPlayer() {
		return player;
	}

	public void setPlayer(T player) {
		this.player = player;
	}

}
