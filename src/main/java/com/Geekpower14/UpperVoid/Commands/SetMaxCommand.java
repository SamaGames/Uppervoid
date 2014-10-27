package com.Geekpower14.UpperVoid.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Arena.Arena;

public class SetMaxCommand implements BasicCommand {

	private UpperVoid plugin;

	public SetMaxCommand(UpperVoid pl) {
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
						+ "Veuillez �crire un nom d'ar�ne correct.");
				return true;
			}

			if (args.length < 2) {
				player.sendMessage(ChatColor.RED + "Please type a number !");
				return true;
			}
			arena.setMaxPlayers(Integer.parseInt(args[1]));
			arena.saveConfig();
			player.sendMessage(ChatColor.GREEN
					+ "Set the max player with success !");

		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv setmax [Arena] [Number] - Set max player in the arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.edit";
	}

}
