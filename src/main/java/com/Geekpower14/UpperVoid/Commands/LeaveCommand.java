package com.Geekpower14.UpperVoid.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Arena.Arena;

public class LeaveCommand implements BasicCommand {

	private UpperVoid plugin;

	public LeaveCommand(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {

		if (UpperVoid.hasPermission(player, this.getPermission())) {
			Arena arena = plugin.am.getArenabyPlayer(player);
			if (arena == null) {
				player.sendMessage(ChatColor.RED + "Vous n'êtes pas en jeux.");
				return true;
			}
			arena.leaveArena(player);
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
