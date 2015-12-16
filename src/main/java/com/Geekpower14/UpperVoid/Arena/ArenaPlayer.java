package com.geekpower14.uppervoid.arena;

import com.geekpower14.uppervoid.stuff.GrapplingHook;
import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.stuff.grenada.Grenada;
import com.geekpower14.uppervoid.Uppervoid;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class ArenaPlayer extends GamePlayer
{
    private final Uppervoid plugin;
    private final Arena arena;
    private final Player player;

    private final HashMap<Integer, Stuff> stuff;
    private final ObjectiveSign objective;

    private Location lastLoc;
    private boolean reloading;

    public ArenaPlayer(Player player)
    {
        super(player);

        this.arena = (Arena) SamaGamesAPI.get().getGameManager().getGame();
        this.plugin = this.arena.getPlugin();

        this.player = player;

        this.stuff = new HashMap<>();
        this.lastLoc = null;
        this.reloading = false;

        this.objective = new ObjectiveSign("infos", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Uppervoid");

        this.updateScoreboard();
        this.loadShop();
    }

    @Override
    public void handleLogout()
    {
        this.objective.removeReceiver(this.player);
    }

    public void loadShop()
    {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
        {
            AbstractShopsManager shopsManager = SamaGamesAPI.get().getShopsManager(this.arena.getGameCodeName());

            String dataRaw = shopsManager.getItemLevelForPlayer(this.player, "shooter");
            Stuff itemByName = this.arena.getItemManager().getItemByName(dataRaw);
            itemByName.setOwner(this);

            this.stuff.put(1, itemByName);

            dataRaw = shopsManager.getItemLevelForPlayer(this.player, "grenade");
            String[] data = dataRaw.split("-");

            Grenada grenada = (Grenada) this.arena.getItemManager().getItemByName("grenade");
            grenada.setUses(1 + Integer.parseInt(data[1]));
            grenada.setOwner(this);

            this.stuff.put(2, grenada);

            dataRaw = shopsManager.getItemLevelForPlayer(this.player, "grapin");
            data = dataRaw.split("-");

            GrapplingHook grapplingHook = (GrapplingHook) this.arena.getItemManager().getItemByName("grapin");
            grapplingHook.setOrigin(1 + Integer.parseInt(data[1]));
            grapplingHook.setUses(1 + Integer.parseInt(data[1]));
            grapplingHook.setOwner(this);

            this.stuff.put(3, grapplingHook);
        });
    }

    public void giveStuff()
    {
        this.stuff.entrySet().forEach(slot -> this.stuff.get(slot.getKey()).getItem());
        this.player.updateInventory();
    }

    public void updateScoreboard()
    {
        this.objective.setLine(0, ChatColor.RED + "");
        this.objective.setLine(1, ChatColor.GRAY + "Pièces " + ChatColor.WHITE + ":" + ChatColor.GOLD + " " + this.coins);
        this.objective.setLine(2, ChatColor.GRAY + "Joueurs " + ChatColor.WHITE + ": " + this.arena.getInGamePlayers().size());
        this.objective.updateLines();
    }

    public void setScoreboard()
    {
        this.objective.addReceiver(this.player);
    }

    public void setReloading(long ticks)
    {
        final long temp = ticks;

        this.reloading = true;
        this.player.setExp(0);

        BukkitTask xpDisplaying = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
        {
            float xp = this.player.getExp();
            xp += (100 / (temp / 2)) / 100;

            if (xp >= 1)
                xp = 1;

            this.player.setExp(xp);
        }, 0L, 2L);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            this.reloading = false;
            this.player.setExp(1);
            xpDisplaying.cancel();
        }, ticks);
    }

    public Stuff getStuff()
    {
        return this.stuff.get(this.player.getInventory().getHeldItemSlot());
    }

    public boolean isOnSameBlock()
    {
        Location location = this.player.getLocation();

        if (this.lastLoc == null)
        {
            this.lastLoc = location;

            return true;
        }

        boolean result = true;

        if (location.getBlockX() != this.lastLoc.getBlockX())
            result = false;

        if (location.getBlockY() != this.lastLoc.getBlockY())
            result = false;

        if (location.getBlockZ() != this.lastLoc.getBlockZ())
            result = false;

        if (!result)
        {
            this.lastLoc = location;
        }

        return result;
    }

    public boolean isReloading()
    {
        return this.reloading;
    }
}