package com.Geekpower14.UpperVoid.Arena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;

public class ArenaManager {

	private UpperVoid plugin;

	private List<Arena> ARENAS = new ArrayList<Arena>();

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
			plugin.log.info(ChatColor.RED + "No Arena found in folder ");
			return;
		}

		plugin.log.info(Maps.size() + " SIZE");
		for (String mapname : Maps) {
			plugin.log.info(ChatColor.GREEN + "Arena " + mapname);

			addArena(mapname);
		}

	}

	public void addArena(String name) {
		if (name == null) {
			return;
		}

		Arena arena = new Arena(plugin, name);

		ARENAS.add(arena);
	}

	public void removeArena(String name) {
		Arena aren = getArena(name);

		aren.stop();

		ARENAS.remove(name);
	}

	public Arena getArena(String name) {
		for (Arena aren : ARENAS) {
			if (aren.getName().equals(name)) {
				return aren;
			}
		}

		return null;
	}

	public void deleteArena(String name) {
		Arena aren = getArena(name);

		aren.stop();

		File file = new File(plugin.getDataFolder(), "/arenas/"
				+ aren.getName() + ".yml");

		file.delete();

		ARENAS.remove(name);

	}

	public boolean exist(String name) {
		if (getArena(name) != null) {
			return true;
		}

		return false;
	}

	public void disable() {
		for (Arena aren : ARENAS) {
			aren.disable();
		}
	}

	public List<String> getArenaNames() {
		List<String> result = new ArrayList<String>();

		for (Arena arena : ARENAS) {
			result.add(arena.getName());
		}

		return result;
	}

	public Arena getArenabyPlayer(Player p) {

		for (Arena arena : ARENAS) {
			if (arena.hasPlayer(p)) {
				return arena;
			}
		}

		return null;
	}

	public Arena getArenabyPlayer(String p) {

		for (Arena arena : ARENAS) {
			if (arena.hasPlayer(p)) {
				return arena;
			}
		}

		return null;
	}

	public List<Arena> getAvailableArenas() {
		List<Arena> result = new ArrayList<Arena>();
		for (Arena arena : ARENAS) {
			if (arena.eta.isLobby()) {
				result.add(arena);
			}
		}

		return result;
	}

	public Arena getRandomArena() {
		List<Arena> a = this.getAvailableArenas();
		int lower = 0;
		int higher = a.size() - 1;

		int random = (int) (Math.random() * (higher - lower)) + lower;
		return a.get(random);
	}

	public int random() {
		List<Arena> a = this.getAvailableArenas();
		int lower = 0;
		int higher = a.size() - 1;

		int random = (int) (Math.random() * (higher - lower)) + lower;

		return random;
	}

	public boolean rejoinNewArena(Player p, int n) {
		if (n >= getAvailableArenas().size()) {
			n = getAvailableArenas().size() - 1;
		}

		Arena arena = getAvailableArenas().get(n);

		if (arena == null) {
			return false;
		}

		arena.joinArena(p);

		return true;
	}

	public boolean isArenaWorld(World w) {
		for (Arena arena : ARENAS) {
			if (arena.getSpawn().getWorld().getName().equals(w.getName())) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(Player p) {
		for (Arena arena : ARENAS) {
			if (arena.isWaiting(p)) {
				return true;
			}
		}

		return false;
	}

	public Arena getWantedPlayerArena(Player p) {
		for (Arena arena : ARENAS) {
			if (arena.isWaiting(p)) {
				return arena;
			}
		}

		return null;
	}

	public List<Arena> getArenas() {
		return ARENAS;
	}

}
