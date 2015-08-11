package com.Geekpower14.UpperVoid.Block;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {

	private UpperVoid plugin;
	private Arena arena;

	private List<BlockGroup> groups = new ArrayList<>();

	private List<RestoreBlock> save = new ArrayList<>();

	private boolean active = true;

	public BlockManager(UpperVoid pl, Arena arena) {
		plugin = pl;
		this.arena = arena;

		loadGroups();
	}

	public void loadGroups() {
		// groups.add(new BlockGroup(plugin, new ABlock(Material.NOTE_BLOCK),
		// new ABlock(Material.JUKEBOX), new ABlock(Material.WOOD, 1)));

        IGameProperties properties = SamaGamesAPI.get().getGameManager().getGameProperties();


        JsonArray spawnDefault = new JsonArray();
        spawnDefault.add(new JsonPrimitive("GRASS, DIRT:1, DIRT:2, "));

        JsonArray spawns = properties.getOption("Blocks", spawnDefault).getAsJsonArray();
        for(JsonElement data : spawns)
        {
            this.groups.add(new BlockGroup(plugin, data.getAsString()));
        }

        /*

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.OBSIDIAN),
				new ABlock(Material.COAL_BLOCK),
				new ABlock(Material.WOOL, 15)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_CLAY, 11),
				new ABlock(Material.STAINED_CLAY, 9),
				new ABlock(Material.STAINED_CLAY, 3)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.PISTON_BASE, 1),
				new ABlock(Material.PISTON_STICKY_BASE, 1),
				new ABlock(Material.WOOD, 1)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.SNOW_BLOCK),
				new ABlock(Material.CLAY),
				new ABlock(Material.DOUBLE_STEP)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.LOG_2, 1),
				new ABlock(Material.LOG, 1),
				new ABlock(Material.LOG)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.NETHER_BRICK),
				new ABlock(Material.NETHERRACK),
				new ABlock(Material.SOUL_SAND)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.GRASS),
				new ABlock(Material.DIRT, 1),
				new ABlock(Material.DIRT, 2)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.LEAVES, 5),
				new ABlock(Material.LEAVES, 4),
				new ABlock(Material.LEAVES, 7)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_CLAY, 5),
				new ABlock(Material.STAINED_CLAY, 4),
				new ABlock(Material.STAINED_CLAY, 14)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.SMOOTH_BRICK),
				new ABlock(Material.SMOOTH_BRICK, 2),
				new ABlock(Material.COBBLESTONE)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.PACKED_ICE),
				new ABlock(Material.ICE),
				new ABlock(Material.STAINED_GLASS, 3)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.QUARTZ_BLOCK),
				new ABlock(Material.QUARTZ_BLOCK, 2),
				new ABlock(Material.QUARTZ_BLOCK, 1)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.SANDSTONE),
				new ABlock(Material.SAND),
				new ABlock(Material.SAND, 1)
		));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_GLASS, 7),
				new ABlock(Material.STAINED_GLASS, 8),
				new ABlock(Material.STAINED_GLASS)
		));
		
		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_CLAY, 13),
				new ABlock(Material.STAINED_CLAY, 1),
				new ABlock(Material.STAINED_CLAY, 14)
		));
				
		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_CLAY, 5),
				new ABlock(Material.STAINED_CLAY, 4),
				new ABlock(Material.STAINED_CLAY, 6)
		));
		
		groups.add(new BlockGroup(plugin,
				new ABlock(Material.LOG, 13),
				new ABlock(Material.LOG, 13),
				new ABlock(Material.LOG, 13)
		));
        */
	}

	public BlockGroup getBlockGroup(Block block) {
		for (BlockGroup bg : groups) {
			if (bg.isThis(block)) {
				return bg;
			}
		}

		return null;
	}

	public boolean isSaved(Block block) {
		for (RestoreBlock rb : save) {
			if (rb.equals(block))
				return true;
		}

		return false;
	}

	public boolean addDamage(Block block) {
		return addDamage(block, 1);
	}

	public boolean addDamage(Block block, int damage) {
		if (!active) {
			return false;
		}

		if (block.getRelative(BlockFace.DOWN).getType() != Material.QUARTZ_BLOCK) {
			return false;
		}

		/*
		 * if(!isSaved(block)) { save.add(new RestoreBlock(plugin, block)); }
		 */

		BlockGroup bg = getBlockGroup(block);

        if(bg == null)
        {
            return false;
        }

		return bg.addDamage(block, damage);
	}

	public void restore() {
		int i = 0;
		for (RestoreBlock rb : save) {
			// rb.restore();
			if (!plugin.isEnabled()) {
				rb.run();
				continue;
			}

			Bukkit.getScheduler().runTaskLater(plugin, rb, i % 10);
			i++;
		}

		save.clear();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean a) {
		active = a;
	}

}
