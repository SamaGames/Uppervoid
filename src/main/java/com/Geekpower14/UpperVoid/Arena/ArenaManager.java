package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.UpperVoid;
import net.samagames.gameapi.GameAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaManager {

	private UpperVoid plugin;

	//private List<Arena> ARENAS = new ArrayList<>();
	private Arena ARENAS = null;

	public ArenaManager(UpperVoid pl) {
		plugin = pl;

		loadArenas();
	}

	public void loadArenas() {
		File folder = new File(plugin.getDataFolder(), "/arenas/");
		if (!folder.exists())
			folder.mkdir();

		List<String> Maps = new ArrayList<String>();

		int zl = 0;
		File afile[];
		if (folder.listFiles() == null)
			return;
		int k = (afile = folder.listFiles()).length;
		for (int j = 0; j < k; j++) {
			File f = afile[j];

			String name = f.getName().replaceAll(".yml", "");
			Maps.add(name);
			plugin.log.info("Found arena : " + name);
			zl++;
		}
		if (zl == 0 || Maps.get(0) == null) {
			plugin.log.info(ChatColor.RED + "No arena found in folder ");
			return;
		}

		plugin.log.info(Maps.size() + " SIZE");
		for (String mapname : Maps) {
			plugin.log.info(ChatColor.GREEN + "arena " + mapname);

			addArena(mapname);
		}

	}

	public void addArena(String name) {
		if (name == null) {
			return;
		}

		Arena arena = new Arena(plugin, name);

        GameAPI.registerArena(arena);
		ARENAS = arena;
	}

	public void removeArena(String name) {
		Arena aren = getArena();

		aren.stop();

		ARENAS = null;
	}

	public Arena getArena() {
		return ARENAS;
	}

	public void deleteArena(String name) {
		Arena aren = getArena();

		aren.stop();

		File file = new File(plugin.getDataFolder(), "/arenas/"
				+ aren.getName() + ".yml");

		file.delete();

		ARENAS = null;

	}

	public boolean exist(String name) {
		if (getArena() != null) {
			return true;
		}

		return false;
	}

	public void disable() {
		ARENAS.disable();
	}

	public Arena getArenabyPlayer(Player p)
	{
		return getArenabyPlayer(p.getName());
	}

	public Arena getArenabyPlayer(String p)
	{
		if(ARENAS.hasPlayer(p))
		{
			return ARENAS;
		}

		return null;
	}

	public Arena getArenaByUUID(UUID uuid)
	{
		if(ARENAS.getUUID().equals(uuid))
		{
			return ARENAS;
		}
		return null;
	}

	public Arena getArenas() {
		return ARENAS;
	}

}
