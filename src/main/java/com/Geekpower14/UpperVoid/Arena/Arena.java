package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.Block.BlockManager;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.StatsNames;
import com.Geekpower14.UpperVoid.Utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.Status;
import net.samagames.api.games.themachine.messages.templates.PlayerWinTemplate;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Arena extends Game<APlayer> {

	public UpperVoid plugin;

	/*** Constantes ***/

	public String Map_name;

	public int minPlayer = 4;
	public int maxPlayer = 24;

	public int coinsGiven = 2; // Pièces données aux joueurs
	public int addCoinsDelay = 20;

	public int Time_Before = 20;
	public int Time_After = 15;

	public Location lobby;

	public List<Location> spawns = new ArrayList<>();

	/*** Variable dynamiques ***/

	private BlockManager blockManager;

	private BukkitTask anticheat;

	//private CoinsGiver coinsGiver;

	// ScoreBoard teams
	private Scoreboard tboard;

    private World.Environment dimension;

	//private Team spectates;

	public Arena(UpperVoid pl) {
		super("uppervoid", "Uppervoid", APlayer.class);

		plugin = pl;

		blockManager = new BlockManager(pl);
		blockManager.setActive(false);

		loadConfig();

        plugin.skyFactory.setDimension(Bukkit.getWorld("world"), dimension);

		tboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
		IGameProperties properties = SamaGamesAPI.get().getGameManager().getGameProperties();

		Map_name = properties.getMapName();

		minPlayer = properties.getMinSlots();
		maxPlayer = properties.getMaxSlots();

		Time_Before = properties.getOption("Time-Before", new JsonPrimitive(30)).getAsInt();
		Time_After = properties.getOption("Time-After", new JsonPrimitive(15)).getAsInt();

        dimension = World.Environment.valueOf(properties.getOption("Dimension", new JsonPrimitive(World.Environment.NORMAL.toString())).getAsString());

        JsonArray spawnDefault = new JsonArray();
        spawnDefault.add(new JsonPrimitive("world, 0, 0, 0, 0, 0"));

        JsonArray spawns = properties.getOption("Spawns", spawnDefault).getAsJsonArray();
        for(JsonElement data : spawns)
        {
            this.spawns.add(Utils.str2loc(data.getAsString()));
        }

		lobby = Utils.str2loc(properties.getOption("Lobby", new JsonPrimitive("world, 0, 0, 0, 0, 0")).getAsString());
	}

	/*** Mouvement des joueurs ***/

	@SuppressWarnings("deprecation")
    public void handleLogin(Player p)
    {
		p.setFlying(false);
		p.setAllowFlight(false);
		cleaner(p);

		p.teleport(lobby);

		APlayer ap = new APlayer(plugin, this, p);
        gamePlayers.put(p.getUniqueId(), ap);

        this.coherenceMachine.getMessageManager().writePlayerJoinToAll(p);

		refresh();

		p.getInventory().setItem(8, this.getLeaveDoor());
		p.getInventory().setHeldItemSlot(0);
		try {
			p.updateInventory();
		} catch (Exception e) {/* LOL */
		}
	}

	/*
	 * Gestion de l'arène.
	 */

    public void handleLogout(Player p)
    {
		APlayer ap = getAPlayer(p);
        p.setAllowFlight(false);
		leaveCleaner(p);

		ap.removeScoreboard();

		updateScorebords();

		refresh();

        if(getStatus() == Status.IN_GAME)
        {
            if(getInGamePlayers().size() == 1)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> win(), 1L);
            }else if(getConnectedPlayers() <= 0){
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> handleGameEnd(), 1L);
			}
		}
	}

    @Override
    public void startGame()
    {
        setStatus(Status.IN_GAME);

		for (APlayer ap : gamePlayers.values()) {
			Player p = ap.getP();

			ap.setScoreboard();

			cleaner(p);
            teleportRandomSpawn(p);

			ap.giveStuff();

			ap.setReloading(6 * 20L);

            increaseStat(p.getUniqueId(), StatsNames.PARTIES, 1);
		}

		int time = 5;// TICKS
		blockManager.setActive(false);
		Bukkit.getScheduler().runTaskLater(plugin, () -> blockManager.setActive(true), time * 20L);

		anticheat = Bukkit.getScheduler().runTaskTimer(plugin,
				() -> gamePlayers.values().stream().filter(ap -> !ap.isSpectator()).forEach(com.Geekpower14.UpperVoid.Arena.APlayer::checkAntiAFK), time * 20L, 20L);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gamePlayers.values().forEach(com.Geekpower14.UpperVoid.Arena.APlayer::updateLastChangeBlock);
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

        super.startGame();
	}

    public void handleGameEnd()
    {
		blockManager.setActive(false);

		super.handleGameEnd();
	}

	public void disable() {
		handleGameEnd();
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
        setStatus(Status.FINISHED);

        anticheat.cancel();

        blockManager.setActive(false);

		Player p = getWinner();

        if(p == null)
        {
            handleGameEnd();
            return;
        }
        final BukkitTask task = generateFirework((int) (Time_After * 1.5), p);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task.cancel();
            handleGameEnd();
        }, (Time_After * 20));

        APlayer ap = getAPlayer(p);
        PlayerWinTemplate template = this.coherenceMachine.getTemplateManager().getPlayerWinTemplate();
        template.execute(p);

        try{
            addStars(p, 1, "Premier en Uppervoid !");
            addCoins(p, 30, "Victoire !");
            ap.setCoins(ap.getCoins() + 30);
            ap.updateScoreboard();
            increaseStat(p.getUniqueId(), StatsNames.VICTOIRES, 1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void refresh()
    {
        plugin.samaGamesAPI.getGameManager().refreshArena();
    }

	@SuppressWarnings("deprecation")
	public void lose(final Player p) {

		APlayer ap = getAPlayer(p);
        setSpectator(p);

		if (getStatus().equals(Status.IN_GAME)) {

            p.sendMessage(coherenceMachine.getGameTag() + ChatColor.YELLOW + "Tu as perdu !");

            int nb = this.getConnectedPlayers();
            coherenceMachine.getMessageManager().writeCustomMessage(p.getName() + ChatColor.YELLOW + " a perdu ! (" + nb
                    + " Joueur" + ((nb > 1) ? "s" : "") + " restant"
                    + ((nb > 1) ? "s" : "") + ")", true);
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(APlayer pp : getAPlayers())
            {
                if(pp.getUUID().equals(p.getUniqueId()))
                    continue;

                if(pp.isSpectator())
                    continue;

                try{
                    addCoins(pp.getP(), 3, "Mort de " + p.getName());
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

		if (this.getConnectedPlayers() <= 1 && getStatus().equals(Status.IN_GAME)) {
			win();
		}

		try{
			updateScorebords();
		}catch(Exception e){}

		cleaner(p);
		teleportRandomSpawn(p);

		p.setGameMode(GameMode.SPECTATOR);

		p.getInventory().setItem(8, this.getLeaveDoor());

		try {
			p.updateInventory();
		} catch (Exception e) {/* LOL */}

		p.setAllowFlight(true);
		p.setFlying(true);

		p.setScoreboard(tboard);
        refresh();
	}

    private Player getWinner() {
		for (APlayer ap : gamePlayers.values()) {
			if (!ap.isSpectator() && ap.isOnline())
			{
				return ap.getP();
			}
		}
		return null;
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

    public void teleportRandomSpawn(Player p)
    {
        p.teleport(spawns.get(new Random().nextInt(spawns.size())));
    }

	public APlayer getAPlayer(Player p) {
		for (APlayer ap : gamePlayers.values()) {
			if (ap.getUUID().equals(p.getUniqueId())) {
				return ap;
			}
		}

		return null;
	}

	public void broadcastXP(int xp) {
		for (APlayer player : gamePlayers.values()) {
			player.setLevel(xp);
		}
	}

	public void playSound(Sound sound, float a, float b) {
		for (APlayer player : gamePlayers.values()) {
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

	public Collection<APlayer> getAPlayers() {
		return gamePlayers.values();
	}

	public int getMinPlayers() {
		return minPlayer;
	}

    public int getMaxPlayers() {
		return maxPlayer;
	}

	public void updateScorebords() {
		for (APlayer ap : gamePlayers.values()) {
			ap.updateScoreboard();
		}
	}

	public void initScorebords() {
		for (APlayer ap : gamePlayers.values()) {
			ap.setScoreboard();
		}
	}

    public Collection<Player> getPlayers() {
        return gamePlayers.values().stream().map(APlayer::getP).collect(Collectors.toList());
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

    public int getTimeBefore() {
		return Time_Before;
	}

	public int getTimeAfter() {
		return Time_After;
	}

	public List<Location> getSpawns() {
		return spawns;
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
}
