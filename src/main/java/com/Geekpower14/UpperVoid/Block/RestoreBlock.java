package com.Geekpower14.UpperVoid.Block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.Geekpower14.UpperVoid.UpperVoid;

public class RestoreBlock implements Runnable {

	private UpperVoid plugin;

	private ABlock b;

	public RestoreBlock(UpperVoid pl, Block block) {
		plugin = pl;

		b = ABlock.getABlock(block);
	}

	public boolean equals(Block block) {
		if (b.getX() != block.getX())
			return false;

		if (b.getY() != block.getY())
			return false;

		if (b.getZ() != block.getZ())
			return false;

		if (!b.getWorld().getName().equals(block.getWorld().getName()))
			return false;

		return true;
	}

	public void restore() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, 1L);
	}

	@Override
	public void run() {
		World w = b.getWorld();

		Block block = w.getBlockAt(b.getX(), b.getY(), b.getZ());
		Block q = w.getBlockAt(b.getX(), b.getY() - 1, b.getZ());

		q.setType(Material.QUARTZ_BLOCK);

		b.setSame(block);
	}

}
