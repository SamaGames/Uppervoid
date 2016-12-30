package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.minecraft.server.v1_10_R1.EntityFishingHook;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.World;
import net.samagames.tools.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GrapplingHook extends Stuff
{
    private int origin;

    public GrapplingHook(Uppervoid plugin, int id, int uses)
    {
        super(plugin, id, new ItemStack(Material.FISHING_ROD, 1), "Grapin", "Vise vite la couche et clic une fois tombé pour remonter !", 2, 5L, true);

        setOrigin(uses);
        setUses(uses);
    }

    @Override
    public void use(ArenaPlayer arenaPlayer)
    {
        Player player = arenaPlayer.getPlayerIfOnline();

        if (!this.canUse() || this.getUses() <= 0)
            return;

        this.setReloading();

        Entity hook = this.spawnFish(((CraftPlayer) player).getHandle());
        hook.setVelocity(player.getLocation().getDirection().multiply(2));
        hook.setTicksLived(60);

        Block block;
        Location eyeLocation = player.getEyeLocation().clone();
        Vector progress = eyeLocation.getDirection().normalize().clone().multiply(0.70);
        Location end = null;
        int maxRange = 100 * 150 / 70;
        int loop = 0;

        while (loop < maxRange)
        {
            loop++;
            eyeLocation.add(progress);
            block = eyeLocation.getBlock();

            if(!block.getType().equals(Material.AIR))
            {
                if (block.getType().equals(Material.QUARTZ_BLOCK))
                    end = eyeLocation;

                break;
            }
        }

        if(end == null)
        {
            arenaPlayer.giveStuff();
            player.sendMessage(ChatColor.RED + "Vous n'arrivez pas à vous accrocher !");
            hook.remove();

            return;
        }

        this.setUses(this.getUses() - 1);

        end.add(0, 2, 0);
        hook.remove();

        ParticleEffect.FIREWORKS_SPARK.display(1F, 2F, 1F, 0.00005F, 10, end, 50);

        player.getWorld().playSound(end, Sound.ENTITY_FIREWORK_LAUNCH, 1.F, 0.01F);
        player.teleport(end);
        player.setVelocity(new Vector(0, 0.5, 0));

        arenaPlayer.giveStuff();
    }

    public Entity spawnFish(EntityHuman entityhuman)
    {
        World world = ((CraftWorld) Bukkit.getWorld("world")).getHandle();
        net.minecraft.server.v1_10_R1.Entity hook = new EntityFishingHook(world, entityhuman);
        world.addEntity(hook);

        return hook.getBukkitEntity();
    }

    public void setOrigin(int origin)
    {
        this.origin = origin;
    }

    @Override
    public ItemStack getItem(ItemStack base)
    {
        base.setDurability((short) (64 - ((64 * this.getUses()) / this.origin)));
        return base;
    }
}
