package com.Geekpower14.UpperVoid.Arena;

import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.Stuff.grapin.Grapin;
import com.Geekpower14.UpperVoid.Stuff.grenade.Grenada;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.Utils;
import net.zyuiop.MasterBundle.FastJedis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.UUID;

public class APlayer {

	public int flag = 0;
	@SuppressWarnings("unused")
	private UpperVoid plugin;
	private Arena arena;
	private Player p;
	private Role role = Role.Player;
	private boolean vip = false;
	private Location lastLoc = null;
	private int DoubleJump = -1;

	private boolean Reloading = false;

	private Scoreboard board;

	private Objective bar;

	private int coins = 0;

	private long lastChangeBlock = System.currentTimeMillis();

	private HashMap<ItemSLot, TItem> stuff = new HashMap<ItemSLot, TItem>();

	public APlayer(UpperVoid pl, Arena arena, Player p) {
		plugin = pl;
		this.arena = arena;
		this.p = p;

		if (Utils.hasPermission(p, "UpperVoid.vip")) {
			vip = true;
		}

		board = Bukkit.getScoreboardManager().getNewScoreboard();

		bar = board.registerNewObjective("Infos", "dummy");

		bar.setDisplaySlot(DisplaySlot.SIDEBAR);
		bar.setDisplayName("" + ChatColor.DARK_AQUA + ChatColor.BOLD
				+ "UpperVoid");
		updateScoreboard();

		resquestStuff();

	}

	public void resquestStuff() {
		if(p.getName().equals("geekpower14"))
		{
			Grapin grapin = (Grapin) plugin.itemManager.getItemByName("grapin");

			grapin.setOrigin_Number(10);
			grapin.setNB(10);
			stuff.put(ItemSLot.Slot4, grapin);
		}
		loadShop();
	}

	public void loadShop() {
		final String key_grenade = "shops:uppervoid:grenades:" + p.getUniqueId() + ":current";
		final String key_shooter = "shops:uppervoid:shooter:" + p.getUniqueId() + ":current";
		final String key_grapin = "shops:uppervoid:grapin:" + p.getUniqueId() + ":current";
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				//Shooter
				String data = FastJedis.get(key_shooter);
				stuff.put(ItemSLot.Slot1, plugin.itemManager.getItemByName(data));

				//Grenade
				data = FastJedis.get(key_grenade);
				if (data != null) {
					String[] dj = data.split("-");
					if (dj[0].equals("grenade")) {
						final int add = Integer.parseInt(dj[1]);
						Grenada grenade = (Grenada) plugin.itemManager.getItemByName("grenada");
						grenade.setNB(1 + add);
						stuff.put(ItemSLot.Slot2, grenade);
					}
				} else {
					Grenada grenade = (Grenada) plugin.itemManager
							.getItemByName("grenada");

					grenade.setNB(1);
					stuff.put(ItemSLot.Slot2, grenade);
				}
				//Grapin
				data = FastJedis.get(key_grapin);
				if (data != null) {
					String[] dj = data.split("-");
					if (dj[0].equals("grapin")) {
						final int add = Integer.parseInt(dj[1]);
						Grapin grapin = (Grapin) plugin.itemManager.getItemByName("grapin");

						grapin.setOrigin_Number(1 + add);
						grapin.setNB(1 + add);
						stuff.put(ItemSLot.Slot3, grapin);
					}
				} else {
					Grapin grapin = (Grapin) plugin.itemManager
							.getItemByName("grapin");
					grapin.setOrigin_Number(1);
					grapin.setNB(1);
					stuff.put(ItemSLot.Slot3, grapin);
				}

			}
		});
	}

	@SuppressWarnings("deprecation")
	public void giveStuff() {
		for (ItemSLot is : stuff.keySet()) {
			TItem item = stuff.get(is);

			p.getInventory().setItem(is.getSlot(), item.getItem());
		}

		p.updateInventory();
	}

    public TItem getStuff()
    {
        return stuff.get(getSlot());
    }

	public ItemSLot getSlot()
    {
		int i = p.getInventory().getHeldItemSlot();
        for(ItemSLot is : ItemSLot.values())
        {
            if(i == is.getSlot())
                return is;
        }

        return null;
    }

	public void removeScoreboard() {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	public void setScoreboard() {
		p.setScoreboard(board);
		updateScoreboard();
	}

	@SuppressWarnings("deprecation")
	public void updateScoreboard() {
		bar.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Coins:"))
				.setScore(coins);
		bar.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Player:"))
				.setScore(arena.getActualPlayers());
		// bar.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD +
		// "DoubleJump:")).setScore(DoubleJump);
	}

	public void setReloading(Long Ticks) {
		Reloading = true;

		final Long temp = Ticks;

		p.setExp(0);

		final int infoxp = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				plugin, new Runnable() {
					public void run() {
						float xp = p.getExp();
						xp += getincr(temp);
						if (xp >= 1) {
							xp = 1;
						}
						p.setExp(xp);
					}
				}, 0L, 2L);

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Reloading = false;
				p.setExp(1);
				plugin.getServer().getScheduler().cancelTask(infoxp);
			}
		}, Ticks);

		return;
	}

	public boolean isReloading() {
		return Reloading;
	}

	public void setReloading(Boolean t) {
		Reloading = t;
	}

	public float getincr(Long time) {
		float result = 0;

		float temp = time;

		result = (100 / (temp / 2)) / 100;

		return result;
	}

	public boolean isVIP() {
		return vip;
	}

	public Arena getArena() {
		return arena;
	}

	/***
	 * 
	 * @return Player p
	 */
	public Player getP() {
		return p;
	}

	public String getName() {
		return p.getName();
	}

	public UUID getUUID() {
		return p.getUniqueId();
	}

	public int getDoubleJump() {
		return DoubleJump;
	}

	public void setDoubleJump(int dj) {
		DoubleJump = dj;
		updateScoreboard();
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int c) {
		coins = c;
		updateScoreboard();
	}

	public String getDisplayName() {
		return p.getDisplayName();
	}

	public Location getLocation() {
		return p.getLocation();
	}

	public Location getEyeLocation() {
		return p.getEyeLocation();
	}

	public boolean isDead() {
		return p.isDead();
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role r) {
		role = r;
	}

	public void tell(String message) {
		p.sendMessage(message);
	}

	public void setLevel(int xp) {
		p.setLevel(xp);
	}

	public void checkAntiAFK() {
		long time = System.currentTimeMillis();

		if (time - lastChangeBlock > 900) {
			Location loc = p.getLocation();

			double X = loc.getX();
			double Y = loc.getBlockY() - 1;
			double Z = loc.getZ();

			Location b = getPlayerStandOnBlockLocation(new Location(
					loc.getWorld(), X, Y, Z));

			arena.getBM().addDamage(b.getBlock());
		}
	}

	private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer) {
		Location b11 = locationUnderPlayer.clone().add(0.3, 0, -0.3);
		if (b11.getBlock().getType() != Material.AIR) {
			return b11;
		}
		Location b12 = locationUnderPlayer.clone().add(-0.3, 0, -0.3);
		if (b12.getBlock().getType() != Material.AIR) {
			return b12;
		}
		Location b21 = locationUnderPlayer.clone().add(0.3, 0, 0.3);
		if (b21.getBlock().getType() != Material.AIR) {
			return b21;
		}
		Location b22 = locationUnderPlayer.clone().add(-0.3, 0, +0.3);
		if (b22.getBlock().getType() != Material.AIR) {
			return b22;
		}
		return locationUnderPlayer;
	}

	public boolean isOnSameBlock() {
		boolean result = true;
		Location loc = p.getLocation();

		if (lastLoc == null) {
			lastLoc = loc;
			return result;
		}

		if (loc.getBlockX() != lastLoc.getBlockX()) {
			result = false;
		}

		if (loc.getBlockY() != lastLoc.getBlockY()) {
			result = false;
		}

		if (loc.getBlockZ() != lastLoc.getBlockZ()) {
			result = false;
		}

		if (result == false) {
			lastLoc = loc;
			lastChangeBlock = System.currentTimeMillis();
		}

		return result;
	}

    public enum ItemSLot{
        // http://redditpublic.com/images/b/b2/Items_slot_number.png
        Head("Head", 103),
        Armor("Armor", 102),
        Slot1("Slot1", 0),
        Slot2("Slot2", 1),
        Slot3("Slot3", 2),
        Slot4("Slot4", 3),
        Slot5("Slot5", 4),
        Slot6("Slot6", 5),
        Slot7("Slot7", 6),
        Slot8("Slot8", 7);

        private String info;
        private int value;

        private ItemSLot(String info, int value)
        {
            this.info = info;
            this.value = value;
        }

        public String getString()
        {
            return info;
        }

        public int getValue()
        {
            return value;
        }

        public int getSlot()
        {
            return value;
        }
    }

	public enum Role {
		Player("Player", 20), Spectator("Spectateur", 10);

		private String info;
		private int value;

		private Role(String info, int value) {
			this.info = info;
			this.value = value;
		}

		public String getString() {
			return info;
		}

		public int getValue() {
			return value;
		}
	}
}
