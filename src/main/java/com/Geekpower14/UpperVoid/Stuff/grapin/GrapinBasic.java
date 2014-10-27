package com.Geekpower14.UpperVoid.Stuff.grapin;

import com.Geekpower14.AdminUtil.Utils.ParticleEffects;
import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.UpperVoid;
import net
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GrapinBasic extends TItem {

	public GrapinBasic(String name, String display, boolean glow, int amount, long reload) {
		super(name, display, glow, amount, reload);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.LEASH, 1);

		ItemMeta item_meta = item.getItemMeta();

		/*coucou_meta.setDisplayName(ChatColor.GOLD + "Shooter " + ChatColor.GRAY
				+ "(Clique-Droit)");*/
        item_meta.setDisplayName(getDisplayName());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(ChatColor.GRAY + "Clique-Droit pour vous agripper à la couche.");
        item_meta.setLore(lores);

		item.setItemMeta(item_meta);

        if(isGlow())
        {
            item = addGlow(item);
        }

		return item;
	}

	@Override
	public void rightAction(APlayer ap) {
		Player p = ap.getP();

		if (ap.getRole() == Role.Spectator)
			return;

		if (ap.isReloading())
			return;

        ap.setReloading(this.reloadTime);

        Entity hook = spawnFish(p.getEyeLocation(), ((CraftPlayer)p).getHandle());
        hook.setVelocity(p.getLocation().getDirection().multiply(2));
        hook.setTicksLived(60);

       // p.setVelocity(new Vector(0, 2, 0));

        Block block;
        Location loc = p.getEyeLocation().clone();
        Location testLoc;
        double lx, ly, lz;
        double px, py, pz;
        // Adapter au format joueur
        Vector progress = loc.getDirection().normalize().clone().multiply(0.70);
        int maxRange = 100;
        maxRange = (100 * maxRange / 70);
        int loop = 0;
        Location fin = null;
        while (loop < maxRange) {
            loop++;
            loc.add(progress);
            block = loc.getBlock();
            if(!block.getType().equals(Material.AIR))
            {
                if (block.getType().equals(Material.QUARTZ_BLOCK))
                {
                    fin = loc;
                    break;
                }else{
                    break;
                }
            }
        }
        if(fin == null)
            return;

        fin.add(0, 2, 0);
        for(Player pp : UpperVoid.getOnline())
        {
            try {
                ParticleEffects.FIREWORKS_SPARK.sendToPlayer(pp, fin, 1F, 2F, 1F, 0.00005F, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        p.getWorld().playSound(fin, Sound.FIREWORK_LAUNCH, 1.F, 0.01F);
        p.teleport(fin);
        p.setVelocity(new Vector(0, 1, 0));

	}

	@Override
	public void leftAction(APlayer ap) {
		return;
	}

    @Override
    public void onItemTouchGround(Arena arena, Item item) {

    }

    public Entity spawnFish(Location location, EntityHuman entityhuman) {
        World world = ((CraftWorld) Bukkit.getWorld("world")).getHandle();
        net.minecraft.server.v1_7_R3.Entity hook = new EntityFishingHook(world, entityhuman);
        world.addEntity(hook);
        entityhuman.aZ(); // Not sure if this is necessary, feel free to play around with it
        return hook.getBukkitEntity();
    }

	@Override
	public GrapinBasic clone() {
		GrapinBasic o = null;
		o = (GrapinBasic) super.clone();
		return o;
	}

}
