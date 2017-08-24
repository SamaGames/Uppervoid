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

/*
 * This file is part of Uppervoid.
 *
 * Uppervoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uppervoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Uppervoid.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 2));

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

                if (this.ticks == 8 * 20)
                    this.cancel();
            }
        }.runTaskTimerAsynchronously(this.plugin, 1L, 1L);
    }

    @Override
    public String getName()
    {
        return ChatColor.AQUA + "Vitesse : 8 secondes";
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
