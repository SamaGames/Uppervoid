package com.Geekpower14.UpperVoid.Listener;

import net.samagames.gameapi.events.FinishJoinPlayerEvent;
import net.samagames.gameapi.json.Status;
import net.zyuiop.statsapi.StatsApi;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.Utils.StatsNames;

public class PlayerListener implements Listener {

	private UpperVoid plugin;

	public PlayerListener(UpperVoid pl) {
		plugin = pl;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent event) {
		/*Player p = event.getPlayer();
		if (!plugin.cm.onPlayerConnect(p)) {
			p.kickPlayer("TO/DO: écrire erreur.");
		}*/

		event.setJoinMessage("");
	}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFinishJoinPlayer(FinishJoinPlayerEvent event) {
        if(!event.isCancelled())
        {
            Player p = Bukkit.getPlayer(event.getPlayer());
            Arena arena = plugin.arenaManager.getArenaByUUID(event.getTargetArena().getUUID());

            if (arena == null)
            {
                event.refuse("arène invalide !");
            }

            arena.joinArena(p);
        }
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();

		event.setQuitMessage("");

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);
		if (arena == null)
			return;

		arena.leaveArena(p);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();

		event.setLeaveMessage("");

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);
		if (arena == null)
			return;
		arena.leaveArena(p);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		final Arena arena = plugin.arenaManager.getArenabyPlayer(player);

		if (arena == null) {
			return;
		}
		event.getRecipients().clear();
		event.getRecipients().addAll(arena.getPlayers());
		// arena.chat(player.getDisplayName()+ ChatColor.GRAY + ": " +
		// event.getMessage());

		return;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		Action action = event.getAction();

		ItemStack hand = player.getItemInHand();

		final Arena arena = plugin.arenaManager.getArenabyPlayer(player);

		if (arena == null) {
			return;
		}
		APlayer ap = arena.getAplayer(player);

		event.setCancelled(true);

		if (hand != null && hand.getType() == Material.WOOD_DOOR) {
			// arena.leaveArena(player);
			arena.kickPlayer(player);
		}

		if (arena.eta != Status.InGame)
			return;

		TItem item = ap.getStuff();
		if (item == null)
			return;

		if (action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK)
			item.leftAction(ap, ap.getSlot());

		if (action == Action.RIGHT_CLICK_AIR
				|| action == Action.RIGHT_CLICK_BLOCK)
			item.rightAction(ap, ap.getSlot());

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerFellOutOfWorld(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID)
		{
			Player p = (Player)event.getEntity();
			Arena arena = plugin.arenaManager.getArenabyPlayer(p);

			if (arena == null)
				return;

			//Si pas commencé
			if (arena.eta.isLobby()) {
				p.teleport(arena.getSpawn());
				return;
			}

			APlayer ap = arena.getAplayer(p);

			//Si Spectateur
			if (ap.getRole() == Role.Spectator) {
				p.teleport(arena.getSpawn());
				p.setAllowFlight(true);
				p.setFlying(true);
				return;
			}

			//Si en jeu
			if (arena.eta == Status.InGame) {
				arena.lose(p);
				return;
			}

			//Si fini
			if (arena.eta == Status.Stopping) {
				p.teleport(arena.getSpawn());
				p.setAllowFlight(true);
				p.setFlying(true);
			}
		}

		return;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);

		if (arena == null)
			return;

		APlayer ap = arena.getAplayer(p);

		if (arena.eta != Status.InGame)
			return;

		// For all.

		if (ap.getRole() == Role.Spectator)
			return;

		/*if ((p.getGameMode() != GameMode.CREATIVE)
				&& (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
				&& (!p.isFlying()) && ap.getDoubleJump() >= 1)
			p.setAllowFlight(true);*/

		/*
		 * if((p.getGameMode() != GameMode.CREATIVE) &&
		 * (p.getLocation().subtract(0, 0.4, 0).getBlock().getType() !=
		 * Material.AIR) && (!p.isFlying())) p.setAllowFlight(false);
		 */
		if(p.getLocation().add(0,0.5,0).getBlock().getType().isSolid())
		{
			arena.kickPlayer(p, ChatColor.RED + "Wall hack");
		}

		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
			return;

		// For players.

		if (!ap.isOnSameBlock()) {
			onPlayerChangePos(arena, p);
		}

		return;
	}

	public void onPlayerChangePos(final Arena arena, final Player p) {
		final Block b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				arena.getBM().addDamage(b);
			}
		}, 5L);
	}

	@EventHandler
	public void onPlayerFish(PlayerFishEvent event)
	{
		Player p = event.getPlayer();

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);

		if (arena == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);

		if (arena == null)
			return;

		APlayer ap = arena.getAplayer(p);

		if (arena.eta != Status.InGame)
			return;

		// For all.

		if (ap.getRole() == Role.Spectator)
			return;

		// For gamers
		if (p.getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);

		if (ap.getDoubleJump() >= 1) {
			p.setFlying(false);
			p.setAllowFlight(false);
			p.setVelocity(p.getLocation().getDirection().normalize()
					.multiply(1.1).setY(1));

			ap.setDoubleJump(ap.getDoubleJump() - 1);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamaged(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player p = (Player) event.getEntity();

		Arena arena = plugin.arenaManager.getArenabyPlayer(p);

		if (arena == null)
			return;

		event.setCancelled(true);

		return;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerClickInventory(InventoryClickEvent event) {
		Arena arena = plugin.arenaManager
				.getArenabyPlayer((Player) event.getWhoClicked());
		if (arena == null) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerOpenInventory(InventoryOpenEvent event) {
		Arena arena = plugin.arenaManager.getArenabyPlayer((Player) event.getPlayer());
		if (arena == null) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Arena arena = plugin.arenaManager.getArenabyPlayer(event.getPlayer());
		if (arena == null) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		Arena arena = plugin.arenaManager.getArenabyPlayer(event.getPlayer());
		if (arena == null) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerFood(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Arena arena = plugin.arenaManager
					.getArenabyPlayer((Player) event.getEntity());
			if (arena == null) {
				return;
			}

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

}
