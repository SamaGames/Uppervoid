package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import net.samagames.tools.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class JumpPowerup extends UppervoidPowerup
{
    private final Random random;

    public JumpPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);

        this.random = new Random();
    }

    @Override
    public void onPickup(Player player)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 8 * 20, 2));

        new BukkitRunnable()
        {
            private int ticks = 0;

            @Override
            public void run()
            {
                final Location location = player.getLocation().subtract(0.0D, 0.75D, 0.0D);

                for(int i = 0; i < 20; i++)
                    ParticleEffect.CLOUD.display(0.25F, 0.25F, 0.25F, 0.0F, 2, location.clone().add(random.nextFloat() - 0.5F, 0.25F, random.nextFloat() - 0.5F), 120.0D);

                this.ticks += 2;

                if (this.ticks == 8 * 20)
                    this.cancel();
            }
        }.runTaskTimerAsynchronously(this.plugin, 2L, 2L);
    }

    @Override
    public String getName()
    {
        return ChatColor.GREEN + "Saut amélioré : 8 secondes";
    }

    @Override
    public ItemStack getIcon()
    {
        return new Potion(PotionType.JUMP).toItemStack(1);
    }

    @Override
    public double getWeight()
    {
        return 15;
    }

    @Override
    public boolean isSpecial()
    {
        return false;
    }
}
