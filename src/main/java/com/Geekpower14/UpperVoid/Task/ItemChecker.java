package com.Geekpower14.UpperVoid.Task;

import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.UpperVoid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemChecker implements Runnable {

	private UpperVoid plugin;

	private int ID;

	private List<Stock> items = new ArrayList<Stock>();

	public ItemChecker(UpperVoid pl) {
		plugin = pl;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 1L, 1L);
	}

	public void addItem(Arena arena, Player p, Item item, TItem ti) {
		items.add(new Stock(arena, p, item, ti));
	}

	public Stock getValue(Arena arena, Player p, Item item) {
		for (Stock ot : items) {
			if (!ot.getArena().getName().equals(arena.getName()))
				continue;
			if ((!ot.getPlayer().getName().equals(p.getName())))
				continue;
			if (ot.getItem().getEntityId() == item.getEntityId())
				return ot;
		}

		return null;
	}

	public void reset(Arena arena) {
        List<Stock> toremove = new ArrayList<Stock>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getArena().getName().equals(arena.getName())) {
                toremove.add(items.get(i));
			}
		}

		toremove.forEach(items::remove);
	}

	public void removeItem(Arena arena, Player p, Item item) {
		items.remove(getValue(arena, p, item));
	}

	public void disable() {
		Bukkit.getScheduler().cancelTask(ID);
	}

	public void run() {

        List<Stock> tmp = new ArrayList<>();

		int size = items.size();
		for (int i = 0; i < size; i++) {
			final Stock s = items.get(i);

			if (s == null || s.getItem() == null || s.getItem().isDead()) {
                tmp.add(s);
            }

			if (s.getItem().isOnGround()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						() -> {
                            // check(s.getArena(), s.getPlayer(),
                            // s.getItem(), s.getItem().getLocation());
                            check(s);
                        });

			}
		}

		tmp.forEach(items::remove);
	}

	public void check(Stock s) {
		Arena arena = s.getArena();
		Item item = s.getItem();
		TItem ti = s.getTItem();
		Player p = s.getPlayer();
		ti.onItemTouchGround(arena, item);

		plugin.itemChecker.removeItem(arena, p, item);
		item.remove();
	}

	public class Stock {

		private Arena arena;
		private Player p;
		private Item item;

		private TItem ti;

		public Stock(Arena arena, Player p, Item item, TItem ti) {
			this.arena = arena;
			this.p = p;
			this.item = item;
			this.ti = ti;
		}

		public TItem getTItem() {
			return ti;
		}

		public void setTItem(TItem item) {
			this.ti = item;
		}

		public Arena getArena() {
			return arena;
		}

		public void setArena(Arena arena) {
			this.arena = arena;
		}

		public Player getPlayer() {
			return p;
		}

		public void setPlayer(Player p) {
			this.p = p;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}
	}

}
