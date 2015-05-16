package com.Geekpower14.UpperVoid.Stuff.grapin;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.Stuff.TItem;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.ParticleEffects;
import net.minecraft.server.v1_8_R1.EntityFishingHook;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GrapinBasic extends TItem {

    public int Origin_Number = 0;

	public GrapinBasic(String name, String display, boolean glow, int amount, long reload) {
		super(name, display, glow, amount, reload);
        Origin_Number = amount;
	}

    public int getOrigin_Number()
    {
        return Origin_Number;
    }

    public void setOrigin_Number(int origin_number)
    {
        Origin_Number = origin_number;
    }

	@Override
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta item_meta = item.getItemMeta();

        item_meta.setDisplayName(getDisplayName());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(ChatColor.GRAY + "Vise vite la couche et clic une fois tombé pour remonter !");
        item_meta.setLore(lores);
		item.setItemMeta(item_meta);
        item.setAmount(getNB());
        item.setDurability((short)(63-((64*(getNB())) / getOrigin_Number())));

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

		if (ap.isReloading() || getNB() <= 0)
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
        int maxRange = 80;
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
        {
            ap.giveStuff();
            p.sendMessage(ChatColor.RED + "Vous n'arrivez pas à vous accrocher !");
            hook.remove();
            return;
        }

        setNB(getNB() - 1);

        fin.add(0, 2, 0);
        for(Player pp : UpperVoid.getOnline())
        {
            try {
                ParticleEffects.FIREWORKS_SPARK.sendToPlayer(pp, fin, 1F, 2F, 1F, 0.00005F, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hook.remove();
        p.getWorld().playSound(fin, Sound.FIREWORK_LAUNCH, 1.F, 0.01F);
        p.teleport(fin);
        p.setVelocity(new Vector(0, 0.5, 0));

        ap.giveStuff();
	}

	@Override
	public void leftAction(APlayer ap, APlayer.ItemSLot slot) {
		rightAction(ap, slot);
        return;
	}

    @Override
    public void onItemTouchGround(Arena arena, Item item) {

    }

    public Entity spawnFish(Location location, EntityHuman entityhuman) {
        World world = ((CraftWorld) Bukkit.getWorld("world")).getHandle();
        net.minecraft.server.v1_8_R1.Entity hook = new EntityFishingHook(world, entityhuman);
        world.addEntity(hook);
        return hook.getBukkitEntity();
    }

	@Override
	public GrapinBasic clone() {
		GrapinBasic o = null;
		o = (GrapinBasic) super.clone();
		return o;
	}

}
