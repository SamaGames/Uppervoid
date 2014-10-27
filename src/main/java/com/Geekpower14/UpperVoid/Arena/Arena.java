package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Block.BlockManager;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.StatsNames;
import com.Geekpower14.UpperVoid.Utils.Utils;
import net.zyuiop.coinsManager.CoinsManager;
import net.zyuiop.statsapi.StatsApi;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

	public UpperVoid plugin;

	/*** Constantes ***/

	public String name;
	public String Map_name;
	public UUID uuid;

	public int minPlayer = 4;
	public int maxPlayer = 24;
	public int vipSlots = 5;
	public int spectateSlots = 5;

	public int coinsGiven = 2; // Pièces données aux joueurs
	public int addCoinsDelay = 20;

	public int Time_Before = 20;
	public int Time_After = 15;

	public Location spawn;

	public boolean vip;

	/*** Variable dynamiques ***/

	public Status eta = Status.Idle;

	public List<APlayer> players = new ArrayList<APlayer>();

	public List<UUID> waitPlayers = new ArrayList<UUID>();

	private Starter CountDown = null;

	private BlockManager bm;

	private int anticheat;

	//private CoinsGiver coinsGiver;

	// ScoreBoard teams
	private Scoreboard tboard;

    private World.Environment dimension;

	//private Team spectates;

	public Arena(UpperVoid pl, String name) {
		plugin = pl;

		this.name = name;

		bm = new BlockManager(pl);

		loadConfig();

        plugin.sf.setDimension(Bukkit.getWorld("world"), dimension);

		tboard = Bukkit.getScoreboardManager().getNewScoreboard();

		/*spectates = tboard.registerNewTeam("spectator");
		spectates.setCanSeeFriendlyInvisibles(true);*/

		eta = Status.Available;

	}

	/*
	 * Configuration.
	 */

	private void loadConfig() {
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(plugin.getDataFolder(), "/arenas/"
						+ name + ".yml"));

		config = basicConfig(config);

		Map_name = config.getString("Map");

		uuid = UUID.fromString(config.getString("UUID"));

		vip = config.getBoolean("VIP");

		Time_Before = config.getInt("Time-Before");
		Time_After = config.getInt("Time-After");

		minPlayer = config.getInt("MinPlayers");
		maxPlayer = config.getInt("MaxPlayers");

		vipSlots = config.getInt("Slots-VIP");
		spectateSlots = config.getInt("Slots-Spectator");

        dimension = World.Environment.valueOf(config.getString("Dimension"));

		spawn = Utils.str2loc(config.getString("Spawn"));

		saveConfig();
	}

	private FileConfiguration basicConfig(FileConfiguration config) {
		setDefaultConfig(config, "Name", name);
		setDefaultConfig(config, "Map", "Unknown");
		setDefaultConfig(config, "UUID", UUID.randomUUID().toString());

		setDefaultConfig(config, "VIP", false);

		setDefaultConfig(config, "Time-Before", 20);
		setDefaultConfig(config, "Time-After", 15);

		setDefaultConfig(config, "MaxPlayers", 24);
		setDefaultConfig(config, "MinPlayers", 4);
		setDefaultConfig(config, "Slots-VIP", 5);
		setDefaultConfig(config, "Slots-Spectator", 5);

        setDefaultConfig(config, "Dimension", World.Environment.NORMAL.toString());

		setDefaultConfig(config, "Spawn", "world, 0, 0, 0, 0, 0");

		return config;
	}

	public void saveConfig() {
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(plugin.getDataFolder(), "/arenas/"
						+ name + ".yml"));

		config.set("Name", name);
		config.set("Map", Map_name);
		config.set("UUID", uuid.toString());

		config.set("VIP", vip);

		config.set("Time-Before", Time_Before);
		config.set("Time-After", Time_After);

		config.set("MinPlayers", minPlayer);
		config.set("MaxPlayers", maxPlayer);
		config.set("Slots-VIP", vipSlots);
		config.set("Slots-Spectator", spectateSlots);

		config.set("Dimension", dimension.toString());

		config.set("Spawn", Utils.loc2str(spawn));

		try {
			config.save(new File(plugin.getDataFolder(), "/arenas/" + name
					+ ".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void setDefaultConfig(FileConfiguration config, String key,
			Object value) {
		if (!config.contains(key))
			config.set(key, value);
	}

	/*** Mouvement des joueurs ***/

	public String requestJoin(final VPlayer p) {

		if (plugin.am.getArenabyPlayer(p.getName()) != null) {
			return ChatColor.RED + "Vous êtes en jeux.";
		}

		if (spawn == null) {
			return ChatColor.RED + "Il n'y a pas de spawn.";
		}

		if (eta.isIG()) {
			return ChatColor.RED + "Jeu en cours.";
		}

        if (CountDown != null && CountDown.time <= 2) {
            return ChatColor.RED + "Jeu en cours.";
        }

		boolean isVIP = p.hasPermission("UpperVoid.vip");
		boolean isAdmin = p.hasPermission("UpperVoid.admin");

		if (getAllWithWait() >= maxPlayer && !isVIP) {
			return ChatColor.RED + "Cette arène est au complet.";
		}

		if (getAllWithWait() >= maxPlayer + vipSlots && !isAdmin) {
			return ChatColor.RED + "Cette arène est au complet.";
		}

		waitPlayers.add(p.getUUID());

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				waitPlayers.remove(p.getUUID());
			}

		}, 40L);

		return "good";
	}

	@SuppressWarnings("deprecation")
	public void joinArena(Player p) {
		if (!waitPlayers.contains(p.getUniqueId())) {
			p.kickPlayer(ChatColor.RED
					+ "Erreur vous n'avez pas le droit de rejoindre.");
			return;
		}

		waitPlayers.remove(p.getUniqueId());

		joinHider(p);
		p.setFlying(false);
		p.setAllowFlight(false);

		cleaner(p);

		p.teleport(spawn);

		APlayer ap = new APlayer(plugin, this, p);
		players.add(ap);

        plugin.cm.sendArenasInfos(true);

		this.broadcast(ChatColor.YELLOW + ap.getName() + " a rejoint l'arène "
				+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + players.size()
				+ ChatColor.DARK_GRAY + "/" + ChatColor.RED + maxPlayer
				+ ChatColor.DARK_GRAY + "]");

		if (players.size() >= minPlayer && eta == Status.Available) {
			startCountdown();
		}

		p.getInventory().setItem(8, this.getLeaveDoor());
		p.getInventory().setHeldItemSlot(0);
		try {
			p.updateInventory();
		} catch (Exception e) {/* LOL */
		}
	}

	public void leaveArena(Player p) {
		APlayer ap = getAplayer(p);

		leaveCleaner(p);

		for (Player pp : Bukkit.getOnlinePlayers()) {
			p.showPlayer(pp);
		}

		ap.removeScoreboard();

		p.setAllowFlight(false);

		// p.setScoreboard(lol);

		players.remove(ap);

		// kickPlayer(p);

		updateScorebords();

        plugin.cm.sendArenasInfos(true);

		if (getActualPlayers() <= 1 && eta == Status.InGame) {
			if (players.size() >= 1) {

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable() {
							@Override
							public void run() {
								win(players.get(0).getP());
							}
						}, 1L);
			} else {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable() {
							@Override
							public void run() {
								stop();
							}
						}, 1L);
			}
		}

		if (players.size() < getMinPlayers() && eta == Status.Starting) {
			resetCountdown();
		}

		return;
	}

	/*
	 * Gestion de l'arène.
	 */

	public void start() {
		eta = Status.InGame;
        plugin.cm.sendArenasInfos(true);

		for (APlayer ap : players) {
			Player p = ap.getP();

			ap.setScoreboard();

			cleaner(p);
			tp(p);

			ap.giveStuff();

			ap.setReloading(6 * 20L);

			StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
					StatsNames.PARTIES, 1);
		}

		int time = 5;// TICKS
		bm.setActive(false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				bm.setActive(true);
			}
		}, time * 20L);

		anticheat = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				new Runnable() {

					@Override
					public void run() {
						for (APlayer ap : players) {
							if (ap.getRole() == Role.Player)
								ap.checkAntiAFK();
						}
					}

				}, time * 20L, 20L);

		/*
		 * coinsGiver = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
		 * new Runnable(){
		 * 
		 * @Override public void run() { for(APlayer ap : players) {
		 * if(ap.getRole() == Role.Player) { //int added =
		 * CoinsManager.creditJoueur(ap.getP().getUniqueId(), coinsGiven, true);
		 * int added = CoinsManager.creditJoueur(ap.getP(), coinsGiven, true);
		 * ap.setCoins(ap.getCoins() + added); ap.updateScoreboard(); } } }
		 * 
		 * }, (time+20) * 20L, addCoinsDelay * 20L);
		 */
		//coinsGiver = new CoinsGiver(this);
		/*Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				coinsGiver.start();
			}
		}, time * 20L);*/

	}

	public void stop() {
		eta = Status.Stopping;
        plugin.cm.sendArenasInfos(true);

		bm.setActive(false);

		/*
		 * TO/DO: Stopper l'ar�ne.
		 */

		/*
		 * List<Player> tokick = new ArrayList<Player>(); for(int i =
		 * players.size()-1; i >= 0; i--) { APlayer ap = players.get(i); Player
		 * p = ap.getP(); tokick.add(p); }
		 * 
		 * for(Player p : tokick) { //leaveArena(p); kickPlayer(p); }
		 */
		for (Player p : Bukkit.getOnlinePlayers())
			kickPlayer(p);

		// bm.restore();

		reset();

		if (plugin.isEnabled()) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					Bukkit.getLogger().info(">>>>> RESTARTING <<<<<");
					Bukkit.getLogger().info("server will reboot now");
					Bukkit.getLogger().info(">>>>> RESTARTING <<<<<");
					Bukkit.getServer().shutdown();

				}
			}, 5 * 20L);
		}
	}

	public void reset() {
		/*
		 * TO/DO: Reset variables.
		 */
		plugin.ghostFactory.clearMembers();

		waitPlayers.clear();
		players.clear();

		// eta = Status.Available;
	}

	public void disable() {
		stop();
		return;
	}

	public void kickPlayer(Player p) {
		kickPlayer(p, "");
	}

	public void kickPlayer(Player p, String msg) {

		if (!plugin.isEnabled()) {
			p.kickPlayer(msg);
			return;
		}
		if (!p.isOnline())
			return;

		// kickPlayer(p, "");
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF("lobby");
		} catch (IOException eee) {
			Bukkit.getLogger().info("You'll never see me!");
		}
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

	}

	public void win(final Player p) {
		eta = Status.Stopping;
        plugin.cm.sendArenasInfos(true);

		if (p != null) {
			APlayer ap = getAplayer(p);
			this.broadcast(ChatColor.AQUA + p.getDisplayName()
					+ ChatColor.YELLOW + " a gagné !");

			int up = CoinsManager.syncCreditJoueur(ap.getP().getUniqueId(), 30,
					true, true);
			// On a besoin de la sync pour l'instruction suivante
			// L'impact sur le lag est minime : une seule requête.

			ap.setCoins(ap.getCoins() + up);
			ap.updateScoreboard();

			for (APlayer a : players) {
				broadcast(a.getP(),
						ChatColor.GOLD + "Tu as gagné " + a.getCoins()
								+ " coins au total !");
			}

			StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
					StatsNames.VICTOIRES, 1);

		}
		Bukkit.getScheduler().cancelTask(anticheat);

		bm.setActive(false);
		if (p == null) {
			stop();
			return;
		}

		final int nb = (int) (Time_After * 1.5);

		final int infoxp = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				this.plugin, new Runnable() {
					int compteur = 0;

					public void run() {

						if (compteur >= nb) {
							return;
						}

						// Spawn the Firework, get the FireworkMeta.
						Firework fw = (Firework) p.getWorld().spawnEntity(
								p.getLocation(), EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();

						// Our random generator
						Random r = new Random();

						// Get the type
						int rt = r.nextInt(4) + 1;
						Type type = Type.BALL;
						if (rt == 1)
							type = Type.BALL;
						if (rt == 2)
							type = Type.BALL_LARGE;
						if (rt == 3)
							type = Type.BURST;
						if (rt == 4)
							type = Type.CREEPER;
						if (rt == 5)
							type = Type.STAR;

						// Get our random colours
						int r1i = r.nextInt(17) + 1;
						int r2i = r.nextInt(17) + 1;
						Color c1 = getColor(r1i);
						Color c2 = getColor(r2i);

						// Create our effect with this
						FireworkEffect effect = FireworkEffect.builder()
								.flicker(r.nextBoolean()).withColor(c1)
								.withFade(c2).with(type).trail(r.nextBoolean())
								.build();

						// Then apply the effect to the meta
						fwm.addEffect(effect);

						// Generate some random power and set it
						int rp = r.nextInt(2) + 1;
						fwm.setPower(rp);

						// Then apply this to our rocket
						fw.setFireworkMeta(fwm);

						compteur++;

					}

				}, 5L, 5L);

		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,
				new Runnable() {

					public void run() {
						plugin.getServer().getScheduler().cancelTask(infoxp);
						stop();

					}

				}, (Time_After * 20));
	}

	@SuppressWarnings("deprecation")
	public void lose(final Player p) {

		APlayer ap = this.getAplayer(p);
		ap.setRole(Role.Spectator);

		updateScorebords();

		if (eta == Status.InGame) {
			broadcast(p, ChatColor.YELLOW + "Tu as perdu !");
			int nb = this.getActualPlayers();
			broadcast(p.getName() + ChatColor.YELLOW + " a perdu ! (" + nb
					+ " Joueur" + ((nb > 1) ? "s" : "") + " restant"
					+ ((nb > 1) ? "s" : "") + ")");
		}

		if (this.getActualPlayers() < 2 && eta == Status.InGame) {
			win(getWin(p));
		}

		cleaner(p);
		p.teleport(getSpawn());

		p.getInventory().setItem(8, this.getLeaveDoor());

		try {
			p.updateInventory();
		} catch (Exception e) {/* LOL */
		}

		p.setAllowFlight(true);
		p.setFlying(true);
        plugin.ghostFactory.setGhost(p, true);
		p.setScoreboard(tboard);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
				Integer.MAX_VALUE, 0));
		loseHider(p);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
        {
            public void run() {
                for(APlayer pp : getAPlayers())
                {
                    if(pp.getUUID().equals(p.getUniqueId()))
                        continue;

                    if(pp.getRole() == Role.Spectator)
                        continue;

                    try{
                        int up = CoinsManager.syncCreditJoueur(pp.getP().getUniqueId(), 3, true, true);
                        pp.setCoins(pp.getCoins() + up);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        plugin.cm.sendArenasInfos(true);
	}

	private Player getWin(Player p) {
		for (APlayer ap : players) {
			if (ap.getRole() == Role.Spectator)
				continue;

			if (!ap.getName().equals(p.getName())) {
				return ap.getP();
			}
		}
		return null;
	}

	public boolean isWaiting(Player p) {
		return isWaiting(p.getUniqueId());
	}

	public boolean isWaiting(UUID uuid) {
		if (waitPlayers.contains(uuid)) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unused")
	private ItemStack creator(Material m, String name, String[] lore) {
		ItemStack lol = new ItemStack(m);

		List<String> l = new ArrayList<String>();
		for (String s : lore) {
			l.add(s);
		}

		ItemMeta me = lol.getItemMeta();
		me.setDisplayName(name);
		me.setLore(l);

		lol.setItemMeta(me);

		return lol;
	}

	public void tp(Player p) {
		if (spawn != null) {
			p.teleport(spawn);
		}
	}

	public APlayer getAplayer(Player p) {
		for (APlayer ap : players) {
			if (ap.getName().equals(p.getName())) {
				return ap;
			}
		}

		return null;
	}

	public void broadcast(String message) {
		for (APlayer player : players) {
			broadcast(player.getP(), message);
		}
	}

	public void broadcast(Player p, String message) {
		p.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "UpperVoid"
				+ ChatColor.DARK_AQUA + "] " + ChatColor.ITALIC + message);
	}

	public void chat(String message) {
		for (APlayer player : players) {
			player.tell(message);
		}
	}

	public void nbroadcast(String message) {
		for (APlayer player : players) {
			player.tell(message);
		}
	}

	public void broadcastXP(int xp) {
		for (APlayer player : players) {
			player.setLevel(xp);
		}
	}

	public void playsound(Sound sound, float a, float b) {
		for (APlayer player : players) {
			player.getP().playSound(player.getP().getLocation(), sound, a, b);
		}
	}

	@SuppressWarnings("deprecation")
	public static void leaveCleaner(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());

		try {
			player.updateInventory();
		} catch (Exception e) {/* LOL */
		}

		return;
	}

	public Collection<Player> getPlayers() {
		List<Player> t = new ArrayList<Player>();
		for (APlayer ap : players)
			t.add(ap.getP());

		return t;
	}

	@SuppressWarnings("deprecation")
	public void cleaner(Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(20F);
		player.setSaturation(20.0F);
		player.setFoodLevel(20);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		player.getInventory().setHeldItemSlot(0);
		player.setExp(0);
		player.setLevel(0);

		try {
			player.updateInventory();
		} catch (Exception e) {/* LOL */
		}

		return;
	}

	public ItemStack getLeaveDoor() {
		ItemStack coucou = new ItemStack(Material.WOOD_DOOR, 1);

		ItemMeta coucou_meta = coucou.getItemMeta();

		coucou_meta.setDisplayName(ChatColor.GOLD + "Quitter l'arène "
				+ ChatColor.GRAY + "(Clique-Droit)");
		coucou.setItemMeta(coucou_meta);

		return coucou;
	}

	public void startCountdown() {
		eta = Status.Starting;

		CountDown = new Starter(plugin, this, Time_Before);

		CountDown.start();
	}

	public void resetCountdown() {
		eta = Status.Available;

		if (CountDown != null) {
			CountDown.abord();
			broadcast(ChatColor.YELLOW + "Compte à rebours remis à zero.");
		}
	}

	public List<APlayer> getAPlayers() {
		return players;
	}

	public void joinHider(Player p) {
		for (Player pp : Bukkit.getOnlinePlayers()) {
			if (!this.hasPlayer(pp)) {
				pp.hidePlayer(p);
				p.hidePlayer(pp);
			} else {
				pp.showPlayer(p);
				p.showPlayer(pp);
			}
		}
	}

	public void loseHider(Player p) {
		for (APlayer ap : players) {
			if (ap.getRole() != Role.Spectator) {
				ap.getP().hidePlayer(p);
			} else if (ap.getRole() == Role.Spectator) {
				ap.getP().showPlayer(p);
				p.showPlayer(ap.getP());
			}
		}
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean hasPlayer(Player p) {
		return hasPlayer(p.getName());
	}

	public boolean hasPlayer(String p) {
		for (APlayer ap : players) {
			if (ap.getName().equals(p)) {
				return true;
			}
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public boolean isVip() {
		return vip;
	}

	public int getMinPlayers() {
		return minPlayer;
	}

	public int getMaxPlayers() {
		return maxPlayer;
	}

	public int getActualPlayers() {
		int nb = 0;

		for (APlayer ap : players) {
			if (ap.getRole() == Role.Player) {
				nb++;
			}
		}

		return nb;
	}

	public int getAllWithWait() {
		return players.size() + waitPlayers.size();
	}

	public void updateScorebords() {
		for (APlayer ap : players) {
			ap.updateScoreboard();
		}
	}

	public void initScorebords() {
		for (APlayer ap : players) {
			ap.setScoreboard();
		}
	}

	public BlockManager getBM() {
		return bm;
	}

	public String getMapName() {
		return Map_name;
	}

	public int getTimeBefore() {
		return Time_Before;
	}

	public int getTimeAfter() {
		return Time_After;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public void setMinPlayers(int nb) {
		minPlayer = nb;
	}

	public void setMaxPlayers(int nb) {
		maxPlayer = nb;
	}

	public void setMapName(String name) {
		Map_name = name;
	}

	public void setTimeBefore(int time) {
		Time_Before = time;
	}

	public void setTimeAfter(int time) {
		Time_After = time;
	}

	public void setSpawn(Location s) {
		spawn = s;
	}

	public int getCountDownRemain() {
		if (CountDown == null)
			return 0;

		return this.CountDown.time;
	}

	public class Starter implements Runnable {

		public int time = 0;

		private UpperVoid plugin;

		private Arena arena;

		private int ID;

		public Starter(UpperVoid pl, Arena aren, int time) {
			this.time = time;
			plugin = pl;
			arena = aren;

		}

		public void abord() {
			Bukkit.getScheduler().cancelTask(ID);
		}

		public void start() {
			arena.broadcast(ChatColor.YELLOW + "Le jeu va démarrer dans "
					+ Time_Before + " secondes.");
			arena.broadcastXP(time);

			ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this,
					1L, 20L);
		}

		@Override
		public void run() {
			arena.broadcastXP(time);

			if (time == 10) {
				arena.broadcast(ChatColor.YELLOW
						+ "Le jeu va démarrer dans 10 secondes.");
				arena.playsound(Sound.NOTE_PLING, 0.6F, 50F);
			}

			if (time <= 5 && time >= 1) {
				arena.broadcast(ChatColor.YELLOW + "Le jeu va démarrer dans "
						+ time + " secondes.");
				arena.playsound(Sound.NOTE_PLING, 0.6F, 50F);
			}

			if (time == 0) {
				arena.playsound(Sound.NOTE_PLING, 9.0F, 1F);
				arena.playsound(Sound.NOTE_PLING, 9.0F, 5F);
				arena.playsound(Sound.NOTE_PLING, 9.0F, 10F);
				arena.broadcast(ChatColor.YELLOW + "C'est parti !");
				arena.start();
			}

			if (time <= 0) {
				abord();
			}
			time--;
		}

	}

	public enum Status {

		Available("available", 80), Idle("idle", 70), Starting("starting", 30), Stopping(
				"stopping", 20), InGame("ingame", 10);

		private String info;
		private int value;

		private Status(String info, int value) {
			this.info = info;
			this.value = value;
		}

		public String getString() {
			return info;
		}

		public int getValue() {
			return value;
		}

		public boolean isLobby() {
			if (value == Status.Available.getValue()
					|| value == Status.Starting.getValue()) {
				return true;
			}

			return false;
		}

		public boolean isIG() {
			if (value == Status.InGame.getValue()
					|| value == Status.Stopping.getValue()) {
				return true;
			}
			return false;
		}

	}

	public Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}
}
