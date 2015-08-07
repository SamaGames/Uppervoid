package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.entity.Player;

public class ArenaManager {

	private UpperVoid plugin;

	//private List<Arena> ARENAS = new ArrayList<>();
	private Arena ARENAS = null;

	public ArenaManager(UpperVoid pl) {
		plugin = pl;

		loadArenas();
	}

	public void loadArenas() {
		/*File folder = new File(plugin.getDataFolder(), "/arenas/");
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
		}*/

		addArena();
	}

	public void addArena() {

		Arena arena = new Arena(plugin);

        plugin.samaGamesAPI.getGameManager().registerGame(arena);
		ARENAS = arena;
	}

	public void removeArena(String name) {
		Arena aren = getArena();

		aren.handleGameEnd();

		ARENAS = null;
	}

	public Arena getArena() {
		return ARENAS;
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
		if(ARENAS.hasPlayer(p))
		{
			return ARENAS;
		}

		return null;
	}


	public Arena getArenas() {
		return ARENAS;
	}

}
