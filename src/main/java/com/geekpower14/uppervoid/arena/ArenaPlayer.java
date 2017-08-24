package com.geekpower14.uppervoid.arena;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.stuff.GrapplingHook;
import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.stuff.grenada.Grenada;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.shops.IPlayerShop;
import net.samagames.api.shops.IShopsManager;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/*
 * This file is part of Uppervoid.
 *
 * Uppervoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uppervoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Uppervoid.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ArenaPlayer extends GamePlayer
{
    private static final int[] SHOOTER_IDs = new int[]{66, 67, 68};
    private static final int SHOOTER_DEFAULT_ID = 66;
    private static final int[] GRENADA_IDs = new int[]{69, 70, 71, 72, 73, 74};
    private static final int GRENADA_DEFAULT_ID = 69;
    private static final int[] GRAPPLING_HOOK_IDs = new int[]{75, 76, 77, 78, 79};
    private static final int GRAPPLING_HOOK_DEFAULT_ID = 75;

    private final Uppervoid plugin;
    private final Arena arena;

    private final HashMap<Integer, Stuff> stuff;
    private final ObjectiveSign objective;

    private Location lastLoc;
    private boolean reloading;

    private long lastChangeBlock = System.currentTimeMillis();

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
            IShopsManager shopsManager = SamaGamesAPI.get().getShopsManager();
            IPlayerShop player = shopsManager.getPlayer(getUUID());

            try
            {
                Stuff itemByName = this.arena.getItemManager().getItemByID(player.getSelectedItemFromList(SHOOTER_IDs));
                itemByName.setOwner(this);

                this.stuff.put(0, itemByName);
            }
            catch (Exception ignored)
            {
                Stuff itemByID = this.arena.getItemManager().getItemByID(SHOOTER_DEFAULT_ID);
                itemByID.setOwner(this);
                this.stuff.put(0, itemByID);
            }

            try
            {

                Grenada grenada = (Grenada) this.arena.getItemManager().getItemByID(player.getSelectedItemFromList(GRENADA_IDs));
                grenada.setOwner(this);

                this.stuff.put(1, grenada);
            }
            catch (Exception ignored)
            {
                Grenada grenada = (Grenada) this.arena.getItemManager().getItemByID(GRENADA_DEFAULT_ID);
                grenada.setOwner(this);

                this.stuff.put(1, grenada);
            }

            try
            {
                GrapplingHook grapplingHook = (GrapplingHook) this.arena.getItemManager().getItemByID(player.getSelectedItemFromList(GRAPPLING_HOOK_IDs));
                grapplingHook.setOwner(this);

                this.stuff.put(2, grapplingHook);
            }
            catch (Exception ignored)
            {
                GrapplingHook grapplingHook = (GrapplingHook) this.arena.getItemManager().getItemByID(GRAPPLING_HOOK_DEFAULT_ID);
                grapplingHook.setOwner(this);

                this.stuff.put(2, grapplingHook);
            }
        });
    }

    public void updateLastChangeBlock()
    {
        this.lastChangeBlock = System.currentTimeMillis();
    }

    public void checkAntiAFK()
    {
        long time = System.currentTimeMillis();

        if (!this.arena.getBlockManager().isActive())
        {
            this.updateLastChangeBlock();
            return;
        }

        long duration = time - lastChangeBlock;

        if (duration > 900)
        {
            Location loc = getPlayerIfOnline().getLocation();

            double x = loc.getX();
            double y = loc.getBlockY() - 1;
            double z = loc.getZ();

            Location block = this.getPlayerStandOnBlockLocation(new Location(loc.getWorld(), x, y, z));

            if(this.arena.getBlockManager().damage(this.uuid, block.getBlock()))
                this.updateLastChangeBlock();
        }
    }

    private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
    {
        Location block = locationUnderPlayer.clone().add(0.3, 0, -0.3);

        if (block.getBlock().getType() != Material.AIR)
            return block;

        block = locationUnderPlayer.clone().add(-0.3, 0, -0.3);

        if (block.getBlock().getType() != Material.AIR)
            return block;

        block = locationUnderPlayer.clone().add(0.3, 0, 0.3);

        if (block.getBlock().getType() != Material.AIR)
            return block;

        block = locationUnderPlayer.clone().add(-0.3, 0, 0.3);

        if (block.getBlock().getType() != Material.AIR)
            return block;

        return locationUnderPlayer;
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
            if (this.getPlayerIfOnline() == null)
                return;

            float xp = this.getPlayerIfOnline().getExp();
            xp += (100 / (temp / 2)) / 100;

            if (xp >= 1)
                xp = 1;

            this.getPlayerIfOnline().setExp(xp);
        }, 0L, 2L);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            if (this.getPlayerIfOnline() == null)
                return;

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