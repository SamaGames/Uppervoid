package com.Geekpower14.UpperVoid.Commands;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StopCommand implements BasicCommand {

	private UpperVoid plugin;

	public StopCommand(UpperVoid pl) {
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

			arena.handleGameEnd();

			player.sendMessage(ChatColor.GREEN
					+ "Force beginning for the arena : " + args[0]);

			return true;
		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return true;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv stop [arena] - Force stop une arene.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.modo";
	}

}