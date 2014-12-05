package com.Geekpower14.UpperVoid;

import com.Geekpower14.UpperVoid.Arena.ArenaManager;
import com.Geekpower14.UpperVoid.Commands.CommandsManager;
import com.Geekpower14.UpperVoid.Listener.PlayerListener;
import com.Geekpower14.UpperVoid.Stuff.ItemManager;
import com.Geekpower14.UpperVoid.Task.ItemChecker;
import com.Geekpower14.UpperVoid.Utils.GhostFactory;
import com.Geekpower14.UpperVoid.Utils.SkyFactory;
import net.samagames.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class UpperVoid extends JavaPlugin {

	public Logger log;

	public static UpperVoid plugin;

	public ArenaManager arenaManager;

	public CommandsManager commandsManager;

	public ItemChecker itemChecker;

	public ItemManager itemManager;
	public int DefaultPort;

	public String BungeeName;

    public SkyFactory skyFactory;

    public GhostFactory ghostFactory;

    public void onEnable() {
		log = getLogger();
		plugin = this;

        DefaultPort = getConfig().getInt("port");
        BungeeName = getConfig().getString("BungeeName");

        GameAPI.registerGame("uppervoid", DefaultPort, BungeeName);

        skyFactory = new SkyFactory(this);

		Bukkit.getWorld("world").setAutoSave(false);

		this.saveDefaultConfig();

        this.ghostFactory = new GhostFactory(this);

		this.getServer().getMessenger()
				.registerOutgoingPluginChannel(this, "BungeeCord");

		itemChecker = new ItemChecker(this);

		arenaManager = new ArenaManager(this);

		itemManager = new ItemManager(this);

		commandsManager = new CommandsManager(this);

		getCommand("uv").setExecutor(commandsManager);

		Bukkit.getPluginManager()
				.registerEvents(new PlayerListener(this), this);
        GameAPI.getManager().sendArenas();
        log.info("UpperVoid enabled!");

	}

	public void onDisable() {
		arenaManager.disable();

		log.info("UpperVoid disabled!");
	}

	public static UpperVoid getPlugin() {
		return plugin;
	}

    public static List<Player> getOnline() {
        List<Player> list = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }

	public static Boolean hasPermission(Player p, String perm) {
		if (perm.equalsIgnoreCase(""))
			return true;
		if (p.isOp())
			return true;
		if (p.hasPermission("UpperVoid.admin"))
			return true;
		if (p.hasPermission(perm))
			return true;

		return false;
	}

}
