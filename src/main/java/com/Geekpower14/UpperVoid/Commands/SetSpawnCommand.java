package com.Geekpower14.UpperVoid.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Arena.Arena;

public class SetSpawnCommand implements BasicCommand {

	private UpperVoid plugin;

	/**
	 * @param pl
	 */
	public SetSpawnCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			Arena arena = null;
			if (plugin.arenaManager.exist(args[0])) {
				arena = plugin.arenaManager.getArena(args[0]);
			}
			if (arena == null) {
				player.sendMessage(ChatColor.RED
						+ "Veuillez �crire un nom d'ar�ne correct.");
				return true;
			}

			arena.setSpawn(player.getLocation());

			arena.saveConfig();
			player.sendMessage(ChatColor.YELLOW + "Spawn d�finis avec succ�s");

		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv setspawn [arena] - Set spawn to an arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.edit";
	}

}
