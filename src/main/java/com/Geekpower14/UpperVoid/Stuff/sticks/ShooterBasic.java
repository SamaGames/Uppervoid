package com.Geekpower14.UpperVoid.Stuff.sticks;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.Utils.StatsNames;
import net.zyuiop.statsapi.StatsApi;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShooterBasic extends TItem {

	public ShooterBasic(String name, String display ,boolean glow, int amount, long reload) {
		super(name, display, glow, amount, reload);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.STICK, 1);

		ItemMeta item_meta = item.getItemMeta();

		/*coucou_meta.setDisplayName(ChatColor.GOLD + "Shooter " + ChatColor.GRAY
				+ "(Clique-Droit)");*/
        item_meta.setDisplayName(getDisplayName());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(ChatColor.GRAY + "se recharge en " + ChatColor.GOLD + reloadTime/20L + ChatColor.GRAY  + ".");
        item_meta.setLore(lores);

		item.setItemMeta(item_meta);

        if(isGlow())
        {
            item = addGlow(item);
        }

		return item;
	}

	@Override
	public void rightAction(APlayer ap, APlayer.ItemSLot slot) {
		Player p = ap.getP();

		if (ap.getRole() == Role.Spectator)
			return;

		if (ap.isReloading())
			return;

		if (!ap.getArena().getBM().isActive())
			return;

		Item tnt = p.getWorld().dropItem(p.getEyeLocation(),
				new ItemStack(Material.TNT));

		tnt.setVelocity(p.getEyeLocation().getDirection().multiply(1.5));

		plugin.itemChecker.addItem(ap.getArena(), p, tnt, this);

		ap.setReloading(this.reloadTime);

		StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
				StatsNames.TNTLaunch, 1);

	}

	@Override
	public void leftAction(APlayer ap, APlayer.ItemSLot slot) {
		return;
	}

	public void onItemTouchGround(Arena arena, Item item) {
		Location center = item.getLocation();

		Block real = center.getBlock().getRelative(BlockFace.DOWN);

		World w = center.getWorld();

		List<Block> damage_1 = new ArrayList<Block>();
		List<Block> damage_2 = new ArrayList<Block>();
		List<Block> damage_3 = new ArrayList<Block>();

		String[] schema = new String[] { "01110", "12221", "12321", "12221",
				"01110" };
		int middle = (schema.length - 1) / 2;

		int ref_x = real.getX() - middle;
		int ref_y = real.getY();
		int ref_z = real.getZ() - middle;

		int incr_x = 0;
		int incr_z = 0;
		for (String str : schema) {
			incr_x = 0;
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);

				if (c == '1') {
					damage_1.add(w.getBlockAt(ref_x + incr_x, ref_y, ref_z
							+ incr_z));
				}

				if (c == '2') {
					damage_2.add(w.getBlockAt(ref_x + incr_x, ref_y, ref_z
							+ incr_z));
				}

				if (c == '3') {
					damage_3.add(w.getBlockAt(ref_x + incr_x, ref_y, ref_z
							+ incr_z));
				}

				incr_x++;
			}
			incr_z++;
		}

		for (Block d1 : damage_1) {
			arena.getBM().addDamage(d1, 1);
		}

		for (Block d2 : damage_2) {
			arena.getBM().addDamage(d2, 2);
		}

		for (Block d3 : damage_3) {
			arena.getBM().addDamage(d3, 3);
		}

		center.getWorld().createExplosion(center.getX(), center.getY(),
				center.getZ(), 1.5F, false, false);

	}

	@Override
	public ShooterBasic clone() {
		ShooterBasic o = null;
		o = (ShooterBasic) super.clone();
		return o;
	}

}
