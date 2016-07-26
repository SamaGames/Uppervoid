package com.geekpower14.uppervoid.listener;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class PlayerListener implements Listener
{
    private final Uppervoid plugin;
    private final Arena arena;

    public PlayerListener(Uppervoid plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = this.arena.getPlayer(player.getUniqueId());

        event.setCancelled(true);

        if (event.getItem() != null && event.getItem().getType() == SamaGamesAPI.get().getGameManager().getCoherenceMachine().getLeaveItem().getType())
        {
            SamaGamesAPI.get().getGameManager().kickPlayer(event.getPlayer(), "");
            return;
        }

        if (!this.arena.getStatus().equals(Status.IN_GAME) || arenaPlayer == null)
            return;

        Stuff item = arenaPlayer.getStuffInHand();

        if (item == null)
            return;

        item.use(arenaPlayer);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        event.setCancelled(true);

        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID)
        {
            Player player = (Player) event.getEntity();

            if (!this.arena.getStatus().equals(Status.IN_GAME) && !this.arena.getStatus().equals(Status.FINISHED))
            {
                player.teleport(this.arena.getLobby());
                return;
            }

            ArenaPlayer arenaPlayer = this.arena.getPlayer(player.getUniqueId());

            if (arenaPlayer == null || arenaPlayer.isSpectator() || this.arena.getStatus().equals(Status.FINISHED))
            {
                this.arena.teleportRandomSpawn(player);
                return;
            }

            if (this.arena.getStatus().equals(Status.IN_GAME))
                this.arena.lose(player);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        event.setCancelled(true);

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball)
        {
            event.getEntity().setVelocity(event.getDamager().getVelocity());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if(event.getFrom().getBlock().equals(event.getTo().getBlock()))
            return;

        ArenaPlayer arenaPlayer = this.arena.getPlayer(player.getUniqueId());

        if (!this.arena.getStatus().equals(Status.IN_GAME))
            return;

        if (arenaPlayer == null || arenaPlayer.isSpectator())
            return;

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
            return;

        if (!arenaPlayer.isOnSameBlock())
        {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
            {
                if (this.arena.getBlockManager().damage(player.getUniqueId(), block))
                {
                    arenaPlayer.updateLastChangeBlock();
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(true);
    }
}
