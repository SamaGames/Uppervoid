package com.Geekpower14.UpperVoid.Block;

import com.Geekpower14.UpperVoid.UpperVoid;
import net.samagames.tools.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockGroup {

	private UpperVoid plugin;

	private ABlock Block_1; // Block neuf.
	private ABlock Block_2; // Block endommagé.
	private ABlock Block_3; // Block pret a peter.

	private ABlock void_; // Block de vide.

	public BlockGroup(UpperVoid pl, String data) {
		plugin = pl;

		String[] parsed = data.split(", ");

		Block_1 = new ABlock(parsed[0]);
		Block_2 = new ABlock(parsed[1]);
		Block_3 = new ABlock(parsed[2]);

		void_ = new ABlock(Material.AIR, (byte) 0);
	}

	public BlockGroup(UpperVoid pl, ABlock type1, ABlock type2, ABlock type3) {
		plugin = pl;

		Block_1 = type1;
		Block_2 = type2;
		Block_3 = type3;

		void_ = new ABlock(Material.AIR, (byte) 0);
	}

	public boolean isThis(Block block) {
		if (block == null) {
			return false;
		}

		if (Block_1.equals(block)) {
			return true;
		}
		if (Block_2.equals(block)) {
			return true;
		}
		if (Block_3.equals(block)) {
			return true;
		}

		return false;
	}

	public boolean isThis(Location loc) {
		if (loc == null) {
			return false;
		}
		Block block = loc.getBlock();

		return isThis(block);
	}

	public boolean addDamage(Block block, int damage) {
		boolean result = false;
		for (int i = 0; i < damage; i++) {
			if (setNext(block))
				result = true;

		}

		if(result)
        {
            ParticleEffect.VILLAGER_HAPPY.display(0.2F, 0.1F, 0.2F, 10F, 1, block.getLocation().add(0.5,1.1,0.5), 50);
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.2F, 0.3F, 0.2F, 10F, 5, block.getLocation().add(0.5,1.1,0.5), 50);
        }
		return result;
	}

	private boolean setNext(Block block) {
		if (block == null) {
			return false;
		}

		if (Block_1.equals(block)) {
			Block_2.setSame(block);
			return true;
		}
		if (Block_2.equals(block)) {
			Block_3.setSame(block);
			return true;
		}
		if (Block_3.equals(block)) {
			void_.setSame(block);
			void_.setSame(block.getRelative(BlockFace.DOWN));
			return true;
		}

		return false;
	}

}
