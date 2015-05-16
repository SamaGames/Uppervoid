package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Block.BlockManager;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.StatsNames;
import com.Geekpower14.UpperVoid.Utils.Utils;
import net.samagames.gameapi.GameAPI;
import net.samagames.gameapi.json.Status;
import net.samagames.gameapi.types.GameArena;
import net.zyuiop.MasterBundle.StarsManager;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena implements GameArena {

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

	public List<APlayer> players = new ArrayList<>();

	public List<UUID> waitPlayers = new ArrayList<>();

	private Starter CountDown = null;

	private BlockManager blockManager;

	private BukkitTask anticheat;

	//private CoinsGiver coinsGiver;

	// ScoreBoard teams
	private Scoreboard tboard;

    private World.Environment dimension;

	//private Team spectates;

	public Arena(UpperVoid pl, String name) {

        plugin = pl;

		this.name = name;

		blockManager = new BlockManager(pl);
		blockManager.setActive(false);

		loadConfig();

        plugin.skyFactory.setDimension(Bukkit.getWorld("world"), dimension);

		tboard = Bukkit.getScoreboardManager().getNewScoreboard();

		/*spectates = tboard.registerNewTeam("spectator");
		spectates.setCanSeeFriendlyInvisibles(true);*/

		eta = Status.Available;
        //GameAPI.getManager().refreshArena(this);
	}

	/*
	 * Configuration.
	 */

	@SuppressWarnings("deprecation")
	public static void leaveCleaner(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());

		try {
			player.updateInventory();
		} catch (Exception e) {/* LOL */}
	}

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

	/*public String requestJoin(final VPlayer p) {

		if (plugin.arenaManager.getArenabyPlayer(p.getName()) != null) {
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
	}*/

	@SuppressWarnings("deprecation")
	public void joinArena(Player p) {
		joinHider(p);
		p.setFlying(false);
		p.setAllowFlight(false);

		cleaner(p);

		p.teleport(spawn);

		APlayer ap = new APlayer(plugin, this, p);
		players.add(ap);

		this.globalTaguedBroadcast(ChatColor.YELLOW + ap.getName() + " a rejoint l'arène "
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
        refresh();
	}

	/*
	 * Gestion de l'arène.
	 */

	public void leaveArena(Player p) {
		APlayer ap = getAPlayer(p);

		leaveCleaner(p);

		UpperVoid.getOnline().forEach(p::showPlayer);

		ap.removeScoreboard();

		p.setAllowFlight(false);

		// p.setScoreboard(lol);

		players.remove(ap);

		// kickPlayer(p);

		updateScorebords();

		if (players.size() < getMinPlayers() && eta == Status.Starting) {
			resetCountdown();
		}

		refresh();

		if (getActualPlayers() <= 1 && eta == Status.InGame) {
			if (players.size() >= 1) {

				win();
			} else {
				stop();
			}
		}
	}

	public void start() {
		eta = Status.InGame;
        refresh();

		for (APlayer ap : players) {
			Player p = ap.getP();

			ap.setScoreboard();

			cleaner(p);
			teleport(p);

			ap.giveStuff();

			ap.setReloading(6 * 20L);

			StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
					StatsNames.PARTIES, 1);
		}

		int time = 5;// TICKS
		blockManager.setActive(false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> blockManager.setActive(true), time * 20L);

		anticheat = Bukkit.getScheduler().runTaskTimer(plugin,
				() -> {
					players.stream().filter(ap -> ap.getRole() == Role.Player).forEach(com.Geekpower14.UpperVoid.Arena.APlayer::checkAntiAFK);
				}, time * 20L, 20L);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			players.forEach(com.Geekpower14.UpperVoid.Arena.APlayer::updateLastChangeBlock);
		}, (time * 20L) - 1);

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
        refresh();

		blockManager.setActive(false);

		/*
		 * TO/DO: Stopper l'arène.
		 */

		UpperVoid.getOnline().forEach(this::kickPlayer);

		// blockManager.restore();

		reset();

		if (plugin.isEnabled()) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getLogger().info(">>>>> RESTARTING <<<<<");
                Bukkit.getLogger().info("server will reboot now");
                Bukkit.getLogger().info(">>>>> RESTARTING <<<<<");
                Bukkit.getServer().shutdown();

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

		p.sendMessage(msg);
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

	public void win() {
		eta = Status.Stopping;
        refresh();

		Player p = getWinner();

		if (p != null) {

			final BukkitTask task = generateFirework((int) (Time_After * 1.5), p);

			Bukkit.getScheduler().runTaskLater(plugin,() -> {
                        task.cancel();
                        stop();
                    }, (Time_After * 20));

			APlayer ap = getAPlayer(p);
			this.globalTaguedBroadcast(ChatColor.AQUA + p.getDisplayName()
					+ ChatColor.YELLOW + " a gagné !");

			StarsManager.creditJoueur(ap.getP(), 1, "Premier en Uppervoid !");
			int up = CoinsManager.syncCreditJoueur(ap.getP().getUniqueId(), 30,
					true, true, "Victoire !");
			// On a besoin de la sync pour l'instruction suivante
			// L'impact sur le lag est minime : une seule requête.

			ap.setCoins(ap.getCoins() + up);
			ap.updateScoreboard();

			for (APlayer a : players) {
				localTaguedBroadcast(a.getP(),
						ChatColor.GOLD + "Tu as gagné " + a.getCoins()
								+ " coins au total !");
			}

			StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
					StatsNames.VICTOIRES, 1);

		}
		anticheat.cancel();

		blockManager.setActive(false);
		if (p == null)
		{
			stop();
		}
	}

    public void refresh()
    {
		final Arena arena = this;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> GameAPI.getManager().refreshArena(arena));
    }

	@SuppressWarnings("deprecation")
	public void lose(final Player p) {

		APlayer ap = getAPlayer(p);
		ap.setRole(Role.Spectator);

		if (eta == Status.InGame) {
			localTaguedBroadcast(p, ChatColor.YELLOW + "Tu as perdu !");
			int nb = this.getActualPlayers();
			globalTaguedBroadcast(p.getName() + ChatColor.YELLOW + " a perdu ! (" + nb
					+ " Joueur" + ((nb > 1) ? "s" : "") + " restant"
					+ ((nb > 1) ? "s" : "") + ")");
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(APlayer pp : getAPlayers())
            {
                if(pp.getUUID().equals(p.getUniqueId()))
                    continue;

                if(pp.getRole() == Role.Spectator)
                    continue;

                try{
                    int up = CoinsManager.syncCreditJoueur(pp.getP().getUniqueId(), 3, true, true, "Mort de " + p.getName());
                    pp.setCoins(pp.getCoins() + up);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

		if (this.getActualPlayers() <= 1 && eta == Status.InGame) {
			win();
		}

		try{
			updateScorebords();
		}catch(Exception e){}

		cleaner(p);
		p.teleport(getSpawn());

		p.setGameMode(GameMode.SPECTATOR);

		p.getInventory().setItem(8, this.getLeaveDoor());

		try {
			p.updateInventory();
		} catch (Exception e) {/* LOL */}

		p.setAllowFlight(true);
		p.setFlying(true);
        plugin.ghostFactory.setGhost(p, true);

		p.setScoreboard(tboard);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
				Integer.MAX_VALUE, 0));
		loseHider(p);

        refresh();
	}

	private Player getWinner() {
		for (APlayer ap : players) {
			if (ap.getRole().equals(Role.Player) && ap.isOnline())
			{
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

	public void teleport(Player p) {
		if (spawn != null) {
			p.teleport(spawn);
		}
	}

	public APlayer getAPlayer(Player p) {
		for (APlayer ap : players) {
			if (ap.getName().equals(p.getName())) {
				return ap;
			}
		}

		return null;
	}

	public void globalTaguedBroadcast(String message) {
		for (APlayer player : players) {
			localTaguedBroadcast(player.getP(), message);
		}
	}

	public void localTaguedBroadcast(Player p, String message) {
		p.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "UpperVoid"
				+ ChatColor.DARK_AQUA + "] " + ChatColor.ITALIC + message);
	}

	public void globalBroadcast(String message) {
		for (APlayer player : players) {
			player.tell(message);
		}
	}

	public void broadcastXP(int xp) {
		for (APlayer player : players) {
			player.setLevel(xp);
		}
	}

	public void playSound(Sound sound, float a, float b) {
		for (APlayer player : players) {
			player.getP().playSound(player.getP().getLocation(), sound, a, b);
		}
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
		} catch (Exception e) {/* LOL */}

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
			globalTaguedBroadcast(ChatColor.YELLOW + "Compte à rebours remis à zero.");
		}
	}

	public List<APlayer> getAPlayers() {
		return players;
	}

	public void joinHider(Player p) {
		for (Player pp : UpperVoid.getOnline()) {
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

    @Override
    public boolean hasPlayer(UUID player) {
        return hasPlayer(Bukkit.getOfflinePlayer(player));
    }

    public boolean hasPlayer(OfflinePlayer p) {
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

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public int getMinPlayers() {
		return minPlayer;
	}

	public void setMinPlayers(int nb) {
		minPlayer = nb;
	}

    @Override
    public int countGamePlayers() {
        return getActualPlayers();
    }

    public int getMaxPlayers() {
		return maxPlayer;
	}

	public void setMaxPlayers(int nb) {
		maxPlayer = nb;
	}

    @Override
    public int getTotalMaxPlayers() {
        return maxPlayer + vipSlots;
    }

    @Override
    public int getVIPSlots() {
        return vipSlots;
    }

    @Override
    public Status getStatus() {
        return eta;
    }

    @Override
    public void setStatus(Status status) {
        eta = status;
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

    public Collection<Player> getPlayers() {
        List<Player> t = new ArrayList<Player>();
        for (APlayer ap : players)
            t.add(ap.getP());

        return t;
    }

	public BlockManager getBM() {
		return blockManager;
	}

	public String getMapName() {
		return Map_name;
	}

	public void setMapName(String name) {
		Map_name = name;
	}

    @Override
    public boolean isFamous() {
        return false;
    }

    public int getTimeBefore() {
		return Time_Before;
	}

	public void setTimeBefore(int time) {
		Time_Before = time;
	}

	public int getTimeAfter() {
		return Time_After;
	}

	public void setTimeAfter(int time) {
		Time_After = time;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location s) {
		spawn = s;
	}

	public int getCountDownRemain() {
		if (CountDown == null)
			return 0;

		return this.CountDown.time;
	}

	public BukkitTask generateFirework(final int nb, final Player player)
	{
		if(player == null)
			return null;

		final BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
					int compteur = 0;

					public void run() {

						if (compteur >= nb) {
							return;
						}

						// Spawn the Firework, get the FireworkMeta.
						Firework fw = (Firework) player.getWorld().spawnEntity(
								player.getLocation(), EntityType.FIREWORK);
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
		return task;
	}

	public Color getColor(int i) {
		if(i==1) return Color.AQUA;
		if(i==2) return Color.BLACK;
		if(i==3) return Color.BLUE;
		if(i==4) return Color.FUCHSIA;
		if(i==5) return Color.GRAY;
		if(i==6) return Color.GREEN;
		if(i==7) return Color.LIME;
		if(i==8) return Color.MAROON;
		if(i==9) return Color.NAVY;
		if(i==10) return Color.OLIVE;
		if(i==11) return Color.ORANGE;
		if(i==12) return Color.PURPLE;
		if(i==13) return Color.RED;
		if(i==14) return Color.SILVER;
		if(i==15) return Color.TEAL;
		if(i==16) return Color.WHITE;
		if(i==17) return Color.YELLOW;
		return null;
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
			arena.globalTaguedBroadcast(ChatColor.YELLOW + "Le jeu va démarrer dans "
					+ Time_Before + " secondes.");
			arena.broadcastXP(time);

			ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this,
					1L, 20L);
		}

		@Override
		public void run() {
			arena.broadcastXP(time);

			if (time == 10) {
				arena.globalTaguedBroadcast(ChatColor.YELLOW
						+ "Le jeu va démarrer dans 10 secondes.");
				arena.playSound(Sound.NOTE_PLING, 0.6F, 50F);
			}

			if (time <= 5 && time >= 1) {
				arena.globalTaguedBroadcast(ChatColor.YELLOW + "Le jeu va démarrer dans "
						+ time + " secondes.");
				arena.playSound(Sound.NOTE_PLING, 0.6F, 50F);
			}

			if(time == 1)
			{
				refresh();
			}

			if (time == 0) {
				arena.playSound(Sound.NOTE_PLING, 9.0F, 1F);
				arena.playSound(Sound.NOTE_PLING, 9.0F, 5F);
				arena.playSound(Sound.NOTE_PLING, 9.0F, 10F);
				arena.globalTaguedBroadcast(ChatColor.YELLOW + "C'est parti !");
				arena.start();
			}

			if (time <= 0) {
				abord();
			}
			time--;
		}

	}
}
