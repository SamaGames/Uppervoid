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

public class SpeedPowerup extends UppervoidPowerup
{
    private final Random random;

    public SpeedPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);

        this.random = new Random();
    }

    @Override
    public void onPickup(Player player)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0));

        new BukkitRunnable()
        {
            private int ticks = 0;

            @Override
            public void run()
            {
                ParticleEffect.ParticleColor color = new ParticleEffect.OrdinaryColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));

                Location location = player.getLocation();
                location.setPitch(0);
                location.add(0, 0.7, 0);

                for (double i = 0; i <= 8; i++)
                {
                    location.add(0, 0.1, 0);
                    ParticleEffect.REDSTONE.display(color, location, 120.0D);
                }

                this.ticks += 1;

                if (this.ticks == 5 * 20)
                    this.cancel();
            }
        }.runTaskTimerAsynchronously(this.plugin, 1L, 1L);
    }

    @Override
    public String getName()
    {
        return ChatColor.AQUA + "Vitesse : 5 secondes";
    }

    @Override
    public ItemStack getIcon()
    {
        return new Potion(PotionType.SPEED).toItemStack(1);
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
