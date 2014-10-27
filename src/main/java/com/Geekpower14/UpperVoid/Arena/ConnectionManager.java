package com.Geekpower14.UpperVoid.Arena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Listener.SocketListener;

public class ConnectionManager {

	private UpperVoid plugin;

	public ServerSocket sock;
	private Thread w;
	private BukkitTask thread;

	private SocketListener sl;

	public ConnectionManager(UpperVoid pl) {
		plugin = pl;

		initInfosSender();

		initListener();
	}

	public boolean onPlayerConnect(Player p) {
		if (!plugin.am.contains(p)) {
			p.sendMessage(ChatColor.RED
					+ "Une erreur s'est produite. Aucune arène ne vous a été assignée.");

			if (p.isOp()) {
				return true;
			}
			return false;
		}

		Arena arena = plugin.am.getWantedPlayerArena(p);
		arena.joinArena(p);

		return true;
	}

	public void initListener() {
		try {
			sock = new ServerSocket(plugin.DefaultPort);
			sl = new SocketListener(plugin, sock);
			w = new Thread(sl);
			w.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initInfosSender() {
		thread = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
				new Runnable() {
					public void run() {
						sendArenasInfos(true);
						//Bukkit.getLogger().info("lol");
					}
				}, 10L, 20L * 30 * 3);
		// is = new InfosSender(plugin);
		// s = new Thread(is);
	}

	public String InputMessage(String msg) {
		String data[] = msg.split(":");
		if (data.length < 1)
			return "bad";
		try {
			if (data[0].equalsIgnoreCase("Join")) {
				/*
				 * 0: Type de packet 1: Nom du joueur 2: UUID 3: Arène
				 */
				if (data.length < 4)
					return "bad";

				String name = data[1];
				UUID uuid = UUID.fromString(data[2]);

				String ar = data[3];
				Arena arena = plugin.am.getArena(ar);

				if (arena == null)
					return "no arena";

				VPlayer vp = new VPlayer(name, uuid);

				return arena.requestJoin(vp);
			}
			if (data[0].equalsIgnoreCase("Start")) {
				/*
				 * 0: Type de packet 1: Nom de l'arène
				 */
				if (data.length < 2)
					return "bad";

				Arena arena = plugin.am.getArena(data[1]);
				if (arena == null)
					return "no arena";

				arena.start();

				return ChatColor.GREEN + "L'arène a bien démarré";
			}

			if (data[0].equalsIgnoreCase("Stop")) {
				/*
				 * 0: Type de packet 1: Nom de l'arène
				 */
				if (data.length < 2)
					return "bad";

				Arena arena = plugin.am.getArena(data[1]);
				if (arena == null)
					return "no arena";

				arena.stop();

				return ChatColor.GREEN + "L'arène a bien stoppée";
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			plugin.log.log(Level.SEVERE, "Data lengh :" + data.length);
			plugin.log.log(Level.SEVERE, "msg :" + msg);
		}
		return "";
	}

	public void disable() {
		// w.interrupt();
		sl.stop();
		// w.interrupt();

		// is.disable();
		// s.interrupt();

		thread.cancel();

		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void sendArenasInfos(final boolean first) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<String> serversLobby = plugin.getConfig().getStringList("Lobbys");

                for (String serv : serversLobby) {
                    final String[] s = serv.split(":");
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sendArenasInfosToServ(s[0], Integer.valueOf(s[1]), first);
                        }
                    });
                }
            }
        });
    }

	public void sendArenasInfosToServ(String ip, int port, boolean first) {
		first = true;
		try {
			InetAddress MainServer = InetAddress.getByName(ip);
			Socket sock = new Socket(MainServer, port);
			PrintWriter out = new PrintWriter(sock.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));

			out.println("56465894869dsfg"); // Authentification de lol.
			out.flush();

			out.println("uppervoid");
			out.flush();

			String b = in.readLine();

			if (!b.equals("good")) {
				sock.close();
				return;
			}

			if (first) {
				// 0 : Type infos
				// 1 : Bungee name
				// 2 : server ip
				// 3 : listen port

				out.println("Infos" + ":" + plugin.BungeeName + ":"
						+ Bukkit.getIp() + ":" + plugin.DefaultPort + ":");
				out.flush();
			} else {
				// 0 : Type ID du serv
				// 1 : nom bungee du serveur

				out.println("ID" + ":" + plugin.BungeeName);
				out.flush();
			}

			for (Arena arena : plugin.am.getArenas()) {
				// 0 : Type
				// 1 : UUID
				// 1 : Arena name
				// 2 : Nb players
				// 3 : Max player
				// 4 : Map Name
				// 5 : ETAT
				// 6 : CountDown Time

				out.println("Arena" + ":" + arena.getUUID().toString() + ":"
						+ arena.name + ":" + arena.getActualPlayers() + ":"
						+ arena.getMaxPlayers() + ":" + arena.Map_name + ":"
						+ arena.eta.getString() + ":"
						+ arena.getCountDownRemain() + ":");
				out.flush();

			}

			out.println("-thisisthend-");
			out.flush();

			sock.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("Erreur envoi au serveur: " + ip + ":" + port);
			// e.printStackTrace();
		}
	}

}
