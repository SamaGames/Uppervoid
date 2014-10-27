package com.Geekpower14.UpperVoid.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Arena.Arena;

public class SaveCommand implements BasicCommand {

	private UpperVoid plugin;

	public SaveCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			Arena arena = null;
			if (plugin.am.exist(args[0])) {
				arena = plugin.am.getArena(args[0]);
			}
			if (arena == null) {
				player.sendMessage(ChatColor.RED
						+ "Veuillez écrire un nom d'arène correct.");
				return true;
			}

			arena.saveConfig();

			player.sendMessage(ChatColor.GREEN
					+ "config saved for the arena : " + args[0]);

		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv save [Arena] - Save config of the arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.edit";
	}

}
