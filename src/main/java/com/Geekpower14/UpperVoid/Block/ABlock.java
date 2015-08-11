package com.Geekpower14.UpperVoid.Block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ABlock {

	private Material type;

	private byte Data = 0;

	private World world;

	private int X;
	private int Y;
	private int Z;

	public ABlock(String data)
    {
        String[] parsed = data.split(":");
        type = Material.matchMaterial(parsed[0]);
        if(parsed.length >= 2)
        {
            Data = (byte) Integer.valueOf(parsed[1]).intValue();
        }
    }

	public ABlock(Material a, int data) {
		type = a;
		Data = (byte) data;
	}

	public ABlock(Material a) {
		this(a, 0);
	}

	@SuppressWarnings("deprecation")
	public static ABlock getABlock(Block block) {
		ABlock ab = new ABlock(block.getType(), block.getData());
		ab.setCoord(block.getWorld(), block.getX(), block.getY(), block.getZ());

		return ab;
	}

	@SuppressWarnings("deprecation")
	public void setSame(Block block) {
		block.setType(type);
		block.setData(Data);
		// block.getWorld().refreshChunk(block.getChunk().getX(),
		// block.getChunk().getZ());
	}

	@SuppressWarnings("deprecation")
	public boolean equals(Block block) {
		if (!block.getType().equals(type)) {
			return false;
		}
		if (block.getData() != Data) {
			return false;
		}
		return true;
	}

	public void setCoord(World w, int x, int y, int z) {
		world = w;
		X = x;
		Y = y;
		Z = z;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World w) {
		world = w;
	}

	public int getX() {
		return X;
	}

	public void setX(int x) {
		X = x;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}

	public int getZ() {
		return Z;
	}

	public void setZ(int z) {
		Z = z;
	}

	public Material getType() {
		return type;
	}

	public byte getData() {
		return Data;
	}

}
