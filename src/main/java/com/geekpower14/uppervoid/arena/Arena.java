package com.geekpower14.uppervoid.arena;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.block.BlockManager;
import com.geekpower14.uppervoid.powerups.*;
import com.geekpower14.uppervoid.stuff.ItemChecker;
import com.geekpower14.uppervoid.stuff.ItemManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.Status;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.PlayerUtils;
import net.samagames.tools.powerups.PowerupManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena extends Game<ArenaPlayer>
{
    private final Uppervoid plugin;
    private final List<Location> spawns;
    private final World.Environment dimension;
    private final Location lobby;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ItemChecker itemChecker;
    private final PowerupManager powerupManager;

    private BukkitTask gameTime;
    private BukkitTask antiAFK;
    private Player second;
    private Player third;

    public Arena(Uppervoid plugin)
    {
        super("uppervoid", "Uppervoid", "Restez au dessus du vide", ArenaPlayer.class);

        this.plugin = plugin;
        this.spawns = new ArrayList<>();

        IGameProperties properties = SamaGamesAPI.get().getGameManager().getGameProperties();

        JsonArray spawnDefault = new JsonArray();
        spawnDefault.add(new JsonPrimitive("world, 0, 0, 0, 0, 0"));

        JsonArray spawnsJson = properties.getOption("spawns", spawnDefault).getAsJsonArray();

        for(int i = 0; i < spawnsJson.size(); i++)
            this.spawns.add(LocationUtils.str2loc(spawnsJson.get(i).getAsString()));

        this.dimension = World.Environment.valueOf(properties.getOption("dimension", new JsonPrimitive(World.Environment.NORMAL.toString())).getAsString());
        this.lobby = LocationUtils.str2loc(properties.getOption("waiting-lobby", new JsonPrimitive("world, 0, 0, 0, 0, 0")).getAsString());

        this.blockManager = new BlockManager();
        this.blockManager.setActive(false);

        this.itemManager = new ItemManager(plugin);

        this.itemChecker = new ItemChecker(plugin);
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this.itemChecker, 1L, 1L);

        this.powerupManager = new PowerupManager(plugin);
        this.powerupManager.setInverseFrequency(250);
        this.powerupManager.registerPowerup(new BlindnessPowerup(plugin, this));
        this.powerupManager.registerPowerup(new SwapPowerup(plugin, this));
        this.powerupManager.registerPowerup(new SpeedPowerup(plugin, this));
        this.powerupManager.registerPowerup(new JumpPowerup(plugin, this));
        this.powerupManager.registerPowerup(new SnowballPowerup(plugin, this));

        JsonArray powerupsSpawnsJson = properties.getOption("powerups-spawns", spawnDefault).getAsJsonArray();

        for(int i = 0; i < powerupsSpawnsJson.size(); i++)
            this.powerupManager.registerLocation(LocationUtils.str2loc(powerupsSpawnsJson.get(i).getAsString()));

        SamaGamesAPI.get().getSkyFactory().setDimension(this.plugin.getServer().getWorld("world"), this.dimension);
    }

    @Override
    public void handleLogin(Player player)
    {
        super.handleLogin(player);

        player.teleport(this.lobby);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(8, this.coherenceMachine.getLeaveItem());
        player.getInventory().setHeldItemSlot(0);
    }

    @Override
    public void handleLogout(Player player)
    {
        ArenaPlayer arenaPlayer = this.getPlayer(player.getUniqueId());
        super.handleLogout(player);

        if(this.getStatus() == Status.IN_GAME && arenaPlayer != null && !arenaPlayer.isSpectator())
        {
            if(this.getInGamePlayers().size() == 1)
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this::win, 1L);
            else if(this.getConnectedPlayers() <= 0)
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this::handleGameEnd, 1L);
            else
                this.lose(player);
        }
    }

    @Override
    public void startGame()
    {
        super.startGame();

        for (ArenaPlayer arenaPlayer : this.gamePlayers.values())
        {
            Player player = arenaPlayer.getPlayerIfOnline();

            player.setWalkSpeed(0.25F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, false, false));

            arenaPlayer.setScoreboard();
            arenaPlayer.giveStuff();
            arenaPlayer.setReloading(6 * 20L);

            this.teleportRandomSpawn(player);
        }

        int time = 4;

        this.blockManager.setActive(false);
        this.powerupManager.start();

        this.getInGamePlayers().values().forEach(ArenaPlayer::updateLastChangeBlock);
        this.antiAFK = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> this.getInGamePlayers().values().forEach(ArenaPlayer::checkAntiAFK), time * 20L, 20L);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.blockManager.setActive(true), time * 20L);

        this.gameTime = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable()
        {
            private int time = 0;

            @Override
            public void run()
            {
                this.time++;
                updateScoreboards(this.formatTime(this.time));
            }

            public String formatTime(int time)
            {
                int mins = time / 60;
                int secs = time - mins * 60;

                String secsSTR = (secs < 10) ? "0" + Integer.toString(secs) : Integer.toString(secs);

                return mins + ":" + secsSTR;
            }
        }, 0L, 20L);
    }

    @Override
    public void handleGameEnd()
    {
        this.blockManager.setActive(false);
        this.gameTime.cancel();
        this.antiAFK.cancel();

        super.handleGameEnd();
    }

    public void win()
    {
        this.setStatus(Status.FINISHED);

        this.blockManager.setActive(false);
        this.powerupManager.stop();

        Player player = this.getWinner();

        if(player == null)
        {
            this.handleGameEnd();
            return;
        }

        this.effectsOnWinner(player);
        this.coherenceMachine.getTemplateManager().getPlayerLeaderboardWinTemplate().execute(player, this.second, this.third);

        this.addStars(player, 1, "Victoire !");
        this.addCoins(player, 30, "Victoire !");
        SamaGamesAPI.get().getStatsManager().getPlayerStats(player.getUniqueId()).getUppervoidStatistics().incrByWins(1);

        this.getPlayer(player.getUniqueId()).updateScoreboard();

        this.handleGameEnd();
    }

    public void lose(Player player)
    {
        this.setSpectator(player);
        this.teleportRandomSpawn(player);

        if (this.getStatus().equals(Status.IN_GAME))
        {
            int left = this.getInGamePlayers().size();
            this.coherenceMachine.getMessageManager().writeCustomMessage(PlayerUtils.getColoredFormattedPlayerName(player) + ChatColor.YELLOW + " a perdu ! (" + left + " joueur" + ((left > 1) ? "s" : "") + " restant" + ((left > 1) ? "s" : "") + ")", true);
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
        {
            for(ArenaPlayer arenaPlayer : this.getInGamePlayers().values())
            {
                if(arenaPlayer.getUUID().equals(player.getUniqueId()))
                    continue;

                this.addCoins(arenaPlayer.getPlayerIfOnline(), 3, "Mort de " + PlayerUtils.getColoredFormattedPlayerName(player));
            }
        });

        if (this.getInGamePlayers().size() == 1 && getStatus().equals(Status.IN_GAME))
        {
            this.second = player;
            this.win();
        }
        else if (this.getInGamePlayers().size() == 2 && getStatus().equals(Status.IN_GAME))
        {
            this.third = player;
        }

        this.updateScoreboards();
    }

    public void updateScoreboards()
    {
        this.gamePlayers.values().forEach(ArenaPlayer::updateScoreboard);
    }

    public void updateScoreboards(String time)
    {
        for (ArenaPlayer arena : this.gamePlayers.values())
            arena.setScoreboardTime(time);
    }

    public void teleportRandomSpawn(Player p)
    {
        p.teleport(this.spawns.get(new Random().nextInt(this.spawns.size())));
    }

    public Player getWinner()
    {
        return this.getInGamePlayers().values().iterator().next().getPlayerIfOnline();
    }

    public Uppervoid getPlugin()
    {
        return this.plugin;
    }

    public BlockManager getBlockManager()
    {
        return this.blockManager;
    }

    public ItemManager getItemManager()
    {
        return this.itemManager;
    }

    public ItemChecker getItemChecker()
    {
        return this.itemChecker;
    }

    public Location getLobby()
    {
        return this.lobby;
    }
}
