package com.Geekpower14.UpperVoid.Arena;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.Geekpower14.UpperVoid.UpperVoid;

public class Server {

	private UpperVoid plugin;

	private String name;

	private String ip;

	private int port;

	public Server(UpperVoid pl, String name, String ip, int port) {
		plugin = pl;

		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public void connectTo(Player p) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(name);
		} catch (IOException eee) {
			Bukkit.getLogger().info("You'll never see me!");
		}
		p.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

	public String getName() {
		return name;
	}

	public String getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}
