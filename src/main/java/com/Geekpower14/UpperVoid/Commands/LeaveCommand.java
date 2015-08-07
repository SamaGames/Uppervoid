package com.Geekpower14.UpperVoid.Commands;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommand implements BasicCommand {

	private UpperVoid plugin;

	public LeaveCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			Arena arena = plugin.arenaManager.getArenabyPlayer(player);
			if (arena == null) {
				player.sendMessage(ChatColor.RED + "Vous n'êtes pas en jeux.");
				return true;
			}
			arena.kickPlayer(player);
		} else {
			player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		}

		return false;
	}

	@Override
	public String help(Player p) {
		if (UpperVoid.hasPermission(p, this.getPermission())) {
			return "/uv leave - Leave an arena.";
		}
		return "";
	}

	@Override
	public String getPermission() {
		return "UpperVoid.player";
	}

}
