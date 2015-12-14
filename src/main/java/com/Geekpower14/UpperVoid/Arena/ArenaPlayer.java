package com.geekpower14.uppervoid.arena;

import com.geekpower14.uppervoid.stuff.GrapplingHook;
import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.stuff.Grenada;
import com.geekpower14.uppervoid.Uppervoid;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class ArenaPlayer extends GamePlayer
{
	// ? public int flag = 0;

	private final Uppervoid plugin;
	private final Arena arena;
	private final Player player;

	private final HashMap<Integer, Stuff> stuff;
    private final ObjectiveSign objective;

    private Location lastLoc;
    private long lastChangeBlock;
    private boolean reloading;

	public ArenaPlayer(Player player)
    {
        super(player);

        this.plugin = Uppervoid.getInstance();
		this.arena = (Arena) SamaGamesAPI.get().getGameManager().getGame();

		this.player = player;

        this.stuff = new HashMap<>();
        this.lastLoc = null;
        this.lastChangeBlock = System.currentTimeMillis();
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

            /*
             * Shooter
             */

            try
            {
                String data = shopsManager.getItemLevelForPlayer(this.player, "shooter");
                Stuff itemByName = this.arena.getItemManager().getItemByName(data);
                itemByName.setOwner(this);

                this.stuff.put(1, itemByName);
            }
            catch (Exception e)
            {
                Stuff shooter = this.arena.getItemManager().getItemByName("shooter");
                shooter.setOwner(this);

                this.stuff.put(1, shooter);
            }

            /*
             * Grenada
             */

            try
            {
                String dataRaw = shopsManager.getItemLevelForPlayer(this.player, "grenade");
                String[] data = dataRaw.split("-");

                if (data[0].equals("grenade"))
                {
                    Grenada grenade = (Grenada) this.arena.getItemManager().getItemByName("grenade");
                    grenade.setUses(1 + Integer.parseInt(data[1]));
                    grenade.setOwner(this);

                    this.stuff.put(2, grenade);
                }
            }
            catch (Exception e)
            {
                Grenada grenade = (Grenada) this.arena.getItemManager().getItemByName("grenade");
                grenade.setUses(1);
                grenade.setOwner(this);

                this.stuff.put(2, grenade);
            }

            /*
             * Grappling hook
             */

            try
            {
                String dataRaw = shopsManager.getItemLevelForPlayer(this.player, "grapin");
                String[] data = dataRaw.split("-");

                if (data[0].equals("grapin"))
                {
                    int add = Integer.parseInt(data[1]);

                    GrapplingHook grapin = (GrapplingHook) this.arena.getItemManager().getItemByName("grapin");
                    grapin.setOrigin(1 + add);
                    grapin.setUses(1 + add);
                    grapin.setOwner(this);

                    this.stuff.put(3, grapin);
                }
            }
            catch (Exception e)
            {
                GrapplingHook grapin = (GrapplingHook) this.arena.getItemManager().getItemByName("grapin");
                grapin.setOrigin(1);
                grapin.setUses(1);
                grapin.setOwner(this);

                this.stuff.put(3, grapin);
            }
        });
	}

	public void giveStuff()
    {
		for (int slot : this.stuff.keySet())
			this.player.getInventory().setItem(slot, this.stuff.get(slot).getItem());

		this.player.updateInventory();
	}

	public void updateScoreboard()
	{
        this.objective.setLine(0, ChatColor.RED + "");
        this.objective.setLine(1, ChatColor.GRAY + "Pièces " + ChatColor.WHITE + ":" + ChatColor.GOLD + " " + this.coins);
        this.objective.setLine(2, ChatColor.GRAY + "Joueurs " + ChatColor.WHITE + ": " + this.arena.getInGamePlayers().size());
        this.objective.updateLines();
	}

    public void updateLastChangeBlock()
    {
        this.lastChangeBlock = System.currentTimeMillis();
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

	private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
    {
		Location b11 = locationUnderPlayer.clone().add(0.3, 0, -0.3);

		if (b11.getBlock().getType() != Material.AIR)
			return b11;

		Location b12 = locationUnderPlayer.clone().add(-0.3, 0, -0.3);

		if (b12.getBlock().getType() != Material.AIR)
			return b12;

		Location b21 = locationUnderPlayer.clone().add(0.3, 0, 0.3);

		if (b21.getBlock().getType() != Material.AIR)
			return b21;

		Location b22 = locationUnderPlayer.clone().add(-0.3, 0, +0.3);

		if (b22.getBlock().getType() != Material.AIR)
			return b22;

		return locationUnderPlayer;
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

    public boolean isOnline()
    {
        if(this.player == null)
            return false;

        return this.player.isOnline();
    }

    public boolean isReloading()
    {
        return this.reloading;
    }
}
