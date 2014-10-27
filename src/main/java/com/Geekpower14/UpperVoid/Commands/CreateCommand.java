package com.Geekpower14.UpperVoid.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;

public class CreateCommand implements BasicCommand {

	private UpperVoid plugin;

	public CreateCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			if (args.length != 1) {
				player.sendMessage(ChatColor.RED
						+ "Veuillez écrire un nom pour l'arène !");
				return true;
			}
			if (plugin.am.exist(args[0])) {
				player.sendMessage(ChatColor.RED + "Arena " + args[0]
						+ " existe déjà !");
				return true;
			}

			plugin.am.addArena(args[0]);
			player.sendMessage(ChatColor.YELLOW + "Arena " + args[0]
					+ " créée avec succés");
		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv create [Arena name] - Create an arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.edit";
	}

}
