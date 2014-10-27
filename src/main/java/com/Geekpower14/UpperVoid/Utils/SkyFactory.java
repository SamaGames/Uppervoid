package com.Geekpower14.UpperVoid.Utils;

import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SkyFactory implements Listener {

        /*
        * Class made by BigTeddy98.
        *
        * SkyFactory is a simple class to change the environment of the sky.
        *
        * 1. No warranty is given or implied.
        * 2. All damage is your own responsibility.
        * 3. If you want to use this in your plugins, a credit would we appreciated.
        */

    private UpperVoid plugin;

    public SkyFactory(UpperVoid plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    //everything needed for our reflection
    private static Constructor<?> packetPlayOutRespawn;
    private static Method getHandle;
    private static Field playerConnection;
    private static Method sendPacket;
    private static Field normal;

    static {
        try {
            //get the packet's constructor
            packetPlayOutRespawn = getMCClass("PacketPlayOutRespawn").getConstructor(int.class, getMCClass("EnumDifficulty"), getMCClass("WorldType"), getMCClass("EnumGamemode"));
            //get CraftPlayer's handle
            getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            //get the PlayerConnection
            playerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
            //get the sendPacket method
            sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket", getMCClass("Packet"));
            //get the field to specify the worldtype for the packet
            normal = getMCClass("WorldType").getDeclaredField("NORMAL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // easy way to get NMS classes
    private static Class<?> getMCClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "net.minecraft.server." + version + name;
        return Class.forName(className);
    }

    // easy way to get CraftBukkit classes
    private static Class<?> getCraftClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "org.bukkit.craftbukkit." + version + name;
        return Class.forName(className);
    }

    //list of changed environments
    private Map<String, Environment> worldEnvironments = new HashMap<String, Environment>();

    public void setDimension(World w, Environment env) {
        worldEnvironments.put(w.getName(), env);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Player p = event.getPlayer();
        //only continue if the world environment is changed
        if (this.worldEnvironments.containsKey(p.getWorld().getName())) {
            //get the EntityPlayer
            Object nms_entity = getHandle.invoke(p);
            //get the connection
            Object nms_connection = playerConnection.get(nms_entity);
            //send the packet
            sendPacket.invoke(nms_connection, getPacket(p));
        }
    }

    @EventHandler
    private void onRespawn(final PlayerRespawnEvent event) {

        //same as onJoin, but execute 1 tick later, otherwise the packet will be ignored by the client because the client is still respawning
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Player p = event.getPlayer();
                    if (worldEnvironments.containsKey(p.getWorld().getName())) {
                        Object nms_entity = getHandle.invoke(p);
                        Object nms_connection = playerConnection.get(nms_entity);
                        sendPacket.invoke(nms_connection, getPacket(p));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(plugin, 1);
    }

    private Object getPacket(Player p) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        World w = p.getWorld();
        //create the new packet instance;
        return packetPlayOutRespawn.newInstance(getID(this.worldEnvironments.get(w.getName())), getDifficulty(w), getLevel(w), getGameMode(p));
    }

    //convert Bukkit environment to NMS environment ID
    private int getID(Environment env) {
        if (env == Environment.NETHER) {
            return -1;
        } else if (env == Environment.NORMAL) {
            return 0;
        } else if (env == Environment.THE_END) {
            return 1;
        }
        return -1;
    }

    //loop through the NMS difficulty enum, and check if it equals the Bukkit difficulty
    private Object getDifficulty(World w) throws ClassNotFoundException {
        for (Object dif : getMCClass("EnumDifficulty").getEnumConstants()) {
            if (dif.toString().equalsIgnoreCase(w.getDifficulty().toString())) {
                return dif;
            }
        }
        return null;
    }

    //loop through the NMS gamemode enum, and check if it equals the Bukkit gamemode
    private Object getGameMode(Player p) throws ClassNotFoundException {
        for (Object dif : getMCClass("EnumGamemode").getEnumConstants()) {
            if (dif.toString().equalsIgnoreCase(p.getGameMode().toString())) {
                return dif;
            }
        }
        return null;
    }

    //get the level type, normal by default
    private Object getLevel(World w) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        return normal.get(null);
    }
}
