package com.Geekpower14.UpperVoid.Commands;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveCommand implements BasicCommand {

	private UpperVoid plugin;

	public RemoveCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {

			Arena arena = null;
			if (plugin.arenaManager.exist(args[0])) {
				arena = plugin.arenaManager.getArena();
			}
			if (arena == null) {
				player.sendMessage(ChatColor.RED
						+ "Veuillez écrire un nom d'arène correct.");
				return true;
			}

			if (args.length != 1) {
				player.sendMessage(ChatColor.RED + "Please type a number !");
				return true;
			}

			plugin.arenaManager.deleteArena(arena.getName());
			player.sendMessage(ChatColor.GREEN + "arena supprimé avec succés.");
		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv remove [arena name] - Remove an arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.edit";
	}

}
