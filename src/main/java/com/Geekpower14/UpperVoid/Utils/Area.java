package com.Geekpower14.UpperVoid.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Area {

	private Location min;
	private Location max;

	public Area(Location un, Location deux) {
		min = new Location(null, 0, 0, 0);
		max = new Location(null, 0, 0, 0);

		if (un == null || deux == null)
			return;

		// x

		if (un.getX() >= deux.getX()) {
			max.setX(un.getX());
			min.setX(deux.getX());
		} else {
			max.setX(deux.getX());
			min.setX(un.getX());
		}

		// y

		if (un.getY() >= deux.getY()) {
			max.setY(un.getY());
			min.setY(deux.getY());
		} else {
			max.setY(deux.getY());
			min.setY(un.getY());
		}

		// z

		if (un.getZ() >= deux.getZ()) {
			max.setZ(un.getZ());
			min.setZ(deux.getZ());
		} else {
			max.setZ(deux.getZ());
			min.setZ(un.getZ());
		}

		min.setWorld(un.getWorld());
		max.setWorld(un.getWorld());

	}

	public Area(List<String> src) {
		min = new Location(null, 0, 0, 0);
		max = new Location(null, 0, 0, 0);

		if (src == null)
			return;

		if (src.size() < 1 || src.size() > 2)
			return;

		Location un = str2loc(src.get(0));
		Location deux = str2loc(src.get(1));

		// x

		if (un.getX() >= deux.getX()) {
			max.setX(un.getX());
			min.setX(deux.getX());
		} else {
			max.setX(deux.getX());
			min.setX(un.getX());
		}

		// y

		if (un.getY() >= deux.getY()) {
			max.setY(un.getY());
			min.setY(deux.getY());
		} else {
			max.setY(deux.getY());
			min.setY(un.getY());
		}

		// z

		if (un.getZ() >= deux.getZ()) {
			max.setZ(un.getZ());
			min.setZ(deux.getZ());
		} else {
			max.setZ(deux.getZ());
			min.setZ(un.getZ());
		}
		min.setWorld(un.getWorld());
		max.setWorld(un.getWorld());
	}

	public Location getMin() {
		return min;
	}

	public Location getMax() {
		return max;
	}

	public Boolean isInArea(Location loc) {
		if (loc == null)
			return false;
		// x

		if (loc.getX() > max.getX() || min.getX() > loc.getX())
			return false;

		// y

		if (loc.getY() > max.getY() || min.getY() > loc.getY())
			return false;

		// z

		if (loc.getZ() > max.getZ() || min.getZ() > loc.getZ())
			return false;

		return true;
	}

	public Boolean isInLimit(Location loc, int range) {
		if (loc == null)
			return false;
		// x

		if (loc.getX() > max.getX() - range || min.getX() + range > loc.getX())
			return true;

		// y

		/*
		 * if(loc.getY() > max.getY() || min.getY() > loc.getY()) return false;
		 */

		// z

		if (loc.getZ() > max.getZ() - range || min.getZ() + range > loc.getZ())
			return true;

		return false;
	}

	public List<String> ToString() {
		List<String> result = new ArrayList<String>();

		result.add(loc2str(min));

		result.add(loc2str(max));

		return result;
	}

	private Location str2loc(String loc) {
		if (loc == null)
			return null;

		Location res = null;

		String[] loca = loc.split(", ");

		res = new Location(Bukkit.getServer().getWorld(loca[0]),
				Double.parseDouble(loca[1]), Double.parseDouble(loca[2]),
				Double.parseDouble(loca[3]), Float.parseFloat(loca[4]),
				Float.parseFloat(loca[5]));

		return res;
	}

	private String loc2str(Location loc) {
		return "" + loc.getWorld().getName() + ", " + loc.getX() + ", "
				+ loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", "
				+ loc.getPitch();
	}

	public int getSizeX() {
		return max.getBlockX() - min.getBlockX();
	}

	public int getSizeY() {
		return max.getBlockY() - min.getBlockY();
	}

	public int getSizeZ() {
		return max.getBlockZ() - min.getBlockZ();
	}

}
