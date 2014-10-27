package com.Geekpower14.UpperVoid.Utils;

import net.minecraft.server.v1_7_R3.EnumDifficulty;
import net.minecraft.server.v1_7_R3.EnumGamemode;
import net.minecraft.server.v1_7_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R3.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {

    public static void setDimension(Player player, int dimension)
    {
        CraftPlayer cp = (CraftPlayer) player;
        PacketPlayOutRespawn packet = new PacketPlayOutRespawn(dimension, EnumDifficulty.PEACEFUL, WorldType.NORMAL, EnumGamemode.a(player.getGameMode().getValue()));

        cp.getHandle().playerConnection.sendPacket(packet);
        org.bukkit.Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        for (int x = -10; x < 10; x++)
        {
            for (int z = -10; z < 10; z++)
            {
                player.getWorld().refreshChunk(chunk.getX() + x, chunk.getZ() + z);
            }
        }
    }


    public static Location str2loc(String loc) {
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

	public static String loc2str(Location loc) {
		return "" + loc.getWorld().getName() + ", " + loc.getX() + ", "
				+ loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", "
				+ loc.getPitch() + ", ";
	}

	public static Boolean hasPermission(Player p, String perm) {
		if (perm.equalsIgnoreCase(""))
			return true;
		if (p.isOp())
			return true;
		if (p.hasPermission("UpperVoid.admin"))
			return true;
		if (p.hasPermission(perm))
			return true;

		return false;
	}

}
