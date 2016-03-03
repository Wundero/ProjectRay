package me.Wundero.ProjectRay.framework.event;

import java.util.List;

import me.Wundero.ProjectRay.framework.iface.Sendable;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.collect.Lists;

public abstract class JsonEvent extends Event implements Cancellable {

	private HandlerList handlers;
	private boolean cancelled;
	private List<Sendable> objects = Lists.newArrayList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public void setHandlers(HandlerList handlers) {
		this.handlers = handlers;
	}

	public List<Sendable> getObjects() {
		if(objects.isEmpty()) {
			createObjects();
		}
		return objects;
	}
	
	public abstract void createObjects();
	
	public void sendObjects() {
		for(Sendable s : getObjects()) {
			s.send();
		}
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
