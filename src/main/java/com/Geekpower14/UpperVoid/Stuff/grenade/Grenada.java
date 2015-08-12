package com.Geekpower14.UpperVoid.Stuff.grenade;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.GlowEffect;
import net.samagames.tools.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Grenada extends TItem {

    private double timeBeforeExplode = 1.3D;

    public Grenada() {
		super("grenada", "Grenada", false, 2, 10L);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.CLAY_BALL, this.nb);

		ItemMeta item_meta = item.getItemMeta();

		item_meta.setDisplayName(ChatColor.RED + "Grenada " + ChatColor.GRAY
                + "(Clique-Droit)");
		item.setItemMeta(item_meta);

        if(isGlow())
        {
            item = GlowEffect.addGlow(item);
        }

		return item;
	}

	/*@Override
	public void rightAction(APlayer ap) {
		Player p = ap.getP();
		ItemStack it = p.getInventory().getItemInHand();

		if (it == null) {
			return;
		}

		if (!ap.getArena().getBM().isActive())
			return;

		if (it.getAmount() <= 0) {
			return;
		}

		Item tnt = p.getWorld().dropItem(p.getEyeLocation(),
				new ItemStack(Material.COCOA));

		tnt.setVelocity(p.getEyeLocation().getDirection().multiply(1.5));

		plugin.itemChecker.addItem(ap.getArena(), p, tnt, this);

		it.setAmount(it.getAmount() - 1);
		p.getInventory().setItemInHand(it);

		StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
				StatsNames.grenade, 1);
	}*/

    @Override
    public void rightAction(final APlayer ap, APlayer.ItemSLot slot)
    {
        final Player p = ap.getP();
        ItemStack it = p.getInventory().getItemInHand();
        if (it == null || !canUse() || !ap.getArena().getBM().isActive() || it.getAmount() <= 0)
            return;

		setReloading(reloadTime);

        p.getWorld().playSound(p.getLocation(), Sound.STEP_SNOW, 3F, 2.0F);

        final Item tnt = p.getWorld().dropItem(p.getEyeLocation(),
                new ItemStack(Material.DRAGON_EGG));
        tnt.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(1.5));
        tnt.setPickupDelay(Integer.MAX_VALUE);

        new BukkitRunnable() {
            public double time = timeBeforeExplode;
            public Item item = tnt;

            @Override
            public void run() {

                if(item == null || item.isDead())
                {
                    this.cancel();
                    return;
                }

                for(Player p : UpperVoid.getOnline())
                {
                    if(time%2 == 0)
                    {
                        p.getWorld().playSound(item.getLocation(), Sound.NOTE_STICKS, 0.8F, 1.5F);
                    }else
                    {
                        p.getWorld().playSound(item.getLocation(), Sound.NOTE_STICKS, 0.8F, 0.5F);
                    }

                }
				if(item != null && item.isOnGround())
				{
					ParticleEffect.FIREWORKS_SPARK.display(1F, 2F, 1F, 0.00005F, 5, item.getLocation(), 50);
				}

                if(time <= 0)
                {
                    this.cancel();
                    Bukkit.getScheduler().runTask(plugin, () -> onItemTouchGround(ap.getArena(), item));
                    return;
                }

                time-=0.25;
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 5L);
		setNB(getNB() - 1);
        it.setAmount(it.getAmount() - 1);
        p.getInventory().setItemInHand(it);
		ap.giveStuff();
    }

	@Override
	public void leftAction(APlayer ap, APlayer.ItemSLot slot) {
		/*Player p = ap.getP();
		ItemStack it = p.getInventory().getItemInHand();

		if (it == null) {
			return;
		}

		if (!ap.getArena().getBM().isActive())
			return;

		if (it.getAmount() <= 0) {
			return;
		}

		Item tnt = p.getWorld().dropItem(p.getEyeLocation(),
				new ItemStack(Material.COCOA));

		tnt.setVelocity(p.getEyeLocation().getDirection().multiply(-1.5));

		plugin.itemChecker.addItem(ap.getArena(), p, tnt, this);

		it.setAmount(it.getAmount() - 1);
		p.getInventory().setItemInHand(it);

		StatsApi.increaseStat(p.getUniqueId(), StatsNames.GAME_NAME,
				StatsNames.grenade, 1);*/
	}

	public void onItemTouchGround(Arena arena, Item item) {
		Location center = item.getLocation();

		Block real = center.add(0, -0.5, 0).getBlock();

		World w = center.getWorld();

		List<Block> damage_1 = new ArrayList<>();
		List<Block> damage_2 = new ArrayList<>();
		List<Block> damage_3 = new ArrayList<>();

		String[] schema = new String[] {
                "00011111000",
                "00111111100",
				"01112221110",
                "11122222111",
                "11222322211",
                "11223332211",
				"11222322211",
                "11122222111",
                "01112221110",
                "00111111100",
				"00011111000" };
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
				center.getZ(), 2.5F, false, false);

	}

	@Override
	public Grenada clone() {
		Grenada o = null;
		o = (Grenada) super.clone();
		return o;
	}

}
