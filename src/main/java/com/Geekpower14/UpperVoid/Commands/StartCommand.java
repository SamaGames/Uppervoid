package com.Geekpower14.UpperVoid.Commands;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartCommand implements BasicCommand {

	private UpperVoid plugin;

	public StartCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			Arena arena = plugin.arenaManager.getArenabyPlayer(player);
			if (arena == null) {
				player.sendMessage(ChatColor.RED
						+ "Vous n'�tes pas dans une ar�ne!");
				return true;
			}

			arena.startGame();

			player.sendMessage(ChatColor.GREEN
					+ "Force beginning for the arena");

		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv start [arena] - Force start an arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.modo";
	}

}