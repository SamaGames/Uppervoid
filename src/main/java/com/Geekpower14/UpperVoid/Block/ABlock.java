package com.Geekpower14.UpperVoid.Block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ABlock {

	private Material type;

	private byte Data;

	private World world;

	private int X;
	private int Y;
	private int Z;

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

	public void setWorld(World w) {
		world = w;
	}

	public void setX(int x) {
		X = x;
	}

	public void setY(int y) {
		Y = y;
	}

	public void setZ(int z) {
		Z = z;
	}

	public World getWorld() {
		return world;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getZ() {
		return Z;
	}

	public Material getType() {
		return type;
	}

	public byte getData() {
		return Data;
	}

}
