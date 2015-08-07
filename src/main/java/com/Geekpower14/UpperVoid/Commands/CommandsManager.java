package com.Geekpower14.UpperVoid.Commands;

import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class CommandsManager implements CommandExecutor {

	public UpperVoid plugin;

	private HashMap<String, BasicCommand> commands;

	public CommandsManager(UpperVoid pl) {
		plugin = pl;
		commands = new HashMap<String, BasicCommand>();
		loadCommands();
	}

	private void loadCommands() {
		commands.put("leave", new LeaveCommand(plugin));
		commands.put("start", new StartCommand(plugin));
		commands.put("stop", new StopCommand(plugin));
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String commandLabel, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("You need to be a player !");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("uv")) {

			if (args == null || args.length < 1) {
				player.sendMessage(ChatColor.YELLOW + "Plugin By Geekpower14 !"
						+ " Version: Beta 1.0.1");
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				help(player);
				return true;
			}

			String sub = args[0];

			Vector<String> l = new Vector<String>();
			l.addAll(Arrays.asList(args));
			l.remove(0);
			args = (String[]) l.toArray(new String[0]);
			if (!commands.containsKey(sub)) {
				player.sendMessage(ChatColor.RED + "Command dosent exist.");
				player.sendMessage(ChatColor.GOLD + "Type /uv help for help");
				return true;
			}
			try {
				commands.get(sub).onCommand(player, args);
			} catch (Exception e) {
				e.printStackTrace();

				player.sendMessage(ChatColor.RED
						+ "An error occured while executing the command. Check the console");
				player.sendMessage(ChatColor.BLUE + "Type /uv help for help");

			}

			return true;
		}

		return true;
	}

	public void help(Player p) {

		p.sendMessage("/uv <command> <args>");
		for (BasicCommand v : commands.values()) {
			p.sendMessage(ChatColor.GRAY + "- " + v.help(p));
		}
	}

}
