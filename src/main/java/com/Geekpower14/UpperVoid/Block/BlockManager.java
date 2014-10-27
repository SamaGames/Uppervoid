package com.Geekpower14.UpperVoid.Block;

import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {

	private UpperVoid plugin;

	private List<BlockGroup> groups = new ArrayList<BlockGroup>();

	private List<RestoreBlock> save = new ArrayList<RestoreBlock>();

	private boolean active = true;

	public BlockManager(UpperVoid pl) {
		plugin = pl;

		loadGroups();
	}

	public void loadGroups() {
		// groups.add(new BlockGroup(plugin, new ABlock(Material.NOTE_BLOCK),
		// new ABlock(Material.JUKEBOX), new ABlock(Material.WOOD, 1)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.OBSIDIAN),
				new ABlock(Material.COAL_BLOCK), new ABlock(Material.WOOL, 15)));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_CLAY, 11), new ABlock(
						Material.STAINED_CLAY, 9), new ABlock(
						Material.STAINED_CLAY, 3)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.PISTON_BASE, 1),
				new ABlock(Material.PISTON_STICKY_BASE, 1), new ABlock(
						Material.WOOD, 1)));

		groups.add(new BlockGroup(plugin, new ABlock(Material.SNOW_BLOCK),
				new ABlock(Material.CLAY), new ABlock(Material.DOUBLE_STEP)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.LOG_2, 1),
				new ABlock(Material.LOG, 1), new ABlock(Material.LOG)));

		groups.add(new BlockGroup(plugin, new ABlock(Material.NETHER_BRICK),
				new ABlock(Material.NETHERRACK), new ABlock(Material.SOUL_SAND)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.GRASS),
				new ABlock(Material.DIRT, 1), new ABlock(Material.DIRT, 2)));

		groups.add(new BlockGroup(plugin, new ABlock(Material.LEAVES, 5),
				new ABlock(Material.LEAVES, 4), new ABlock(Material.LEAVES, 7)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.STAINED_CLAY, 5),
				new ABlock(Material.STAINED_CLAY, 4), new ABlock(
						Material.STAINED_CLAY, 14)));

		groups.add(new BlockGroup(plugin, new ABlock(Material.SMOOTH_BRICK),
				new ABlock(Material.SMOOTH_BRICK, 2), new ABlock(
						Material.COBBLESTONE)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.PACKED_ICE),
				new ABlock(Material.ICE), new ABlock(Material.STAINED_GLASS, 3)));

		groups.add(new BlockGroup(plugin, new ABlock(Material.QUARTZ_BLOCK),
				new ABlock(Material.QUARTZ_BLOCK, 2), new ABlock(
						Material.QUARTZ_BLOCK, 1)));
		groups.add(new BlockGroup(plugin, new ABlock(Material.SANDSTONE),
				new ABlock(Material.SAND), new ABlock(Material.SAND, 1)));

		groups.add(new BlockGroup(plugin,
				new ABlock(Material.STAINED_GLASS, 7), new ABlock(
						Material.STAINED_GLASS, 8), new ABlock(
						Material.STAINED_GLASS)));
	}

	public BlockGroup getBlockGroup(Block block) {
		for (BlockGroup bg : groups) {
			if (bg.isThis(block)) {
				return bg;
			}
		}

		return null;
	}

	public void setActive(boolean a) {
		active = a;
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

}
