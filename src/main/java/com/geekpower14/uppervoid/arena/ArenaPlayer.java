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
import java.util.logging.Level;

public class ArenaPlayer extends GamePlayer
{
    private static final String SHOOTER_ID = "shooter";
    private static final String GRENADA_ID = "grenade";
    private static final String GRAPPLING_HOOK_ID = "grapin";

    private final Uppervoid plugin;
    private final Arena arena;

    private final HashMap<Integer, Stuff> stuff;
    private final ObjectiveSign objective;

    private Location lastLoc;
    private boolean reloading;

    public ArenaPlayer(Player player)
    {
        super(player);

        this.arena = (Arena) SamaGamesAPI.get().getGameManager().getGame();
        this.plugin = this.arena.getPlugin();

        this.stuff = new HashMap<>();
        this.lastLoc = null;
        this.reloading = false;

        this.objective = new ObjectiveSign("uppervoid", ChatColor.RED + "" + ChatColor.BOLD + "Uppervoid" + ChatColor.WHITE + " | " + ChatColor.AQUA + "00:00");

        this.updateScoreboard();
        this.loadShop();
    }

    @Override
    public void handleLogout()
    {
        this.objective.removeReceiver(this.getOfflinePlayer());
    }

    public void loadShop()
    {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
        {
            AbstractShopsManager shopsManager = SamaGamesAPI.get().getShopsManager(this.arena.getGameCodeName());

            try
            {
                String dataRaw = shopsManager.getItemLevelForPlayer(this.getUUID(), SHOOTER_ID);
                Stuff itemByName = this.arena.getItemManager().getItemByName(dataRaw);
                itemByName.setOwner(this);

                this.stuff.put(0, itemByName);
            }
            catch (Exception ignored)
            {
                this.stuff.put(0, this.arena.getItemManager().getItemByName(SHOOTER_ID));
            }

            try
            {
                String dataRaw = shopsManager.getItemLevelForPlayer(this.getUUID(), GRENADA_ID);
                String[] data = dataRaw.split("-");

                Grenada grenada = (Grenada) this.arena.getItemManager().getItemByName(GRENADA_ID);
                grenada.setUses(1 + Integer.parseInt(data[1]));
                grenada.setOwner(this);

                this.stuff.put(1, grenada);
            }
            catch (Exception ignored)
            {
                Grenada grenada = (Grenada) this.arena.getItemManager().getItemByName(GRENADA_ID);
                grenada.setUses(1);
                grenada.setOwner(this);

                this.stuff.put(1, grenada);
            }

            try
            {
                String dataRaw = shopsManager.getItemLevelForPlayer(this.getUUID(), GRAPPLING_HOOK_ID);
                String[] data = dataRaw.split("-");

                GrapplingHook grapplingHook = (GrapplingHook) this.arena.getItemManager().getItemByName(GRAPPLING_HOOK_ID);
                grapplingHook.setOrigin(1 + Integer.parseInt(data[1]));
                grapplingHook.setUses(1 + Integer.parseInt(data[1]));
                grapplingHook.setOwner(this);

                this.stuff.put(2, grapplingHook);
                this.plugin.getLogger().info("Grappling level " + (1 + Integer.parseInt(data[1])) + " loaded for player " + this.uuid);
            }
            catch (Exception ignored)
            {
                GrapplingHook grapplingHook = (GrapplingHook) this.arena.getItemManager().getItemByName(GRAPPLING_HOOK_ID);
                grapplingHook.setOrigin(1);
                grapplingHook.setUses(1);
                grapplingHook.setOwner(this);

                this.stuff.put(2, grapplingHook);

                this.plugin.getLogger().log(Level.WARNING, "Error while getting grappling for player " + this.uuid, ignored);
            }
        });
    }

    public void checkAntiAFK()
    {
        if(!this.arena.getBlockManager().isActive())
            return;

        Location location = this.getPlayerIfOnline().getLocation();

        double x = location.getX();
        double y = location.getBlockY() - 1.0D;
        double z = location.getZ();

        this.arena.getBlockManager().damage(new Location(location.getWorld(), x, y, z).getBlock());
    }

    public void giveStuff()
    {
        this.stuff.keySet().forEach(slot -> this.getPlayerIfOnline().getInventory().setItem(slot, this.stuff.get(slot).getItem()));
        this.getPlayerIfOnline().updateInventory();
    }

    public void updateScoreboard()
    {
        this.objective.setLine(0, ChatColor.RED + "");
        this.objective.setLine(1, ChatColor.GRAY + "Pièces " + ChatColor.WHITE + ":" + ChatColor.GOLD + " " + this.coins);
        this.objective.setLine(2, ChatColor.GRAY + "");
        this.objective.setLine(3, ChatColor.GRAY + "Joueurs " + ChatColor.WHITE + ": " + this.arena.getInGamePlayers().size());
        this.objective.updateLines();
    }


    public void setScoreboard()
    {
        this.objective.addReceiver(this.getOfflinePlayer());
    }

    public void setScoreboardTime(String time)
    {
        this.objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Uppervoid" + ChatColor.WHITE + " | " + ChatColor.AQUA + time);
        this.updateScoreboard();
    }

    public void setReloading(long ticks)
    {
        final long temp = ticks;

        this.reloading = true;
        this.getPlayerIfOnline().setExp(0);

        BukkitTask xpDisplaying = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
        {
            float xp = this.getPlayerIfOnline().getExp();
            xp += (100 / (temp / 2)) / 100;

            if (xp >= 1)
                xp = 1;

            this.getPlayerIfOnline().setExp(xp);
        }, 0L, 2L);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            this.reloading = false;
            this.getPlayerIfOnline().setExp(1);

            xpDisplaying.cancel();
        }, ticks);
    }

    public Stuff getStuffInHand()
    {
        return this.stuff.get(this.getPlayerIfOnline().getInventory().getHeldItemSlot());
    }

    public boolean isOnSameBlock()
    {
        Location location = this.getPlayerIfOnline().getLocation();

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
            this.lastLoc = location;

        return result;
    }

    public boolean isReloading()
    {
        return this.reloading;
    }
}