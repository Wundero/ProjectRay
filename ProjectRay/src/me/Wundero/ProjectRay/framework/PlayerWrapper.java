package me.Wundero.ProjectRay.framework;

import java.util.UUID;

public abstract class PlayerWrapper<T> {

	public PlayerWrapper(T player) {
		load(player);
	}

	public abstract UUID getUUID();

	public UUID getUniqueId() {
		return getUUID();
	}

	public abstract void setUUID(UUID uuid);

	public abstract void load(T player);

	public abstract String getLastName();

	public abstract void setLastName(String lastName);

}
