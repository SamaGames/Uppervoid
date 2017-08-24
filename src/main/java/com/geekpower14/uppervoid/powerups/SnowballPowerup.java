package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import net.samagames.tools.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
public class SnowballPowerup extends UppervoidPowerup
{
    public SnowballPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        new BukkitRunnable()
        {
            private int ticks = 0;

            @Override
            public void run()
            {
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(snowball.getVelocity().multiply(1.75));

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        if (!snowball.isDead())
                            ParticleEffect.FLAME.display(0.05F, 0.05F, 0.05F, 0.1F, 4, snowball.getLocation(), 120.0D);
                        else
                            this.cancel();
                    }
                }.runTaskTimer(plugin, 1L, 1L);

                this.ticks += 10;

                if (this.ticks == 6 * 20)
                    this.cancel();
            }
        }.runTaskTimer(this.plugin, 10L, 10L);
    }

    @Override
    public String getName()
    {
        return ChatColor.WHITE + "Canon à boule de neige : 6 secondes";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.SNOW_BALL, 1);
    }

    @Override
    public double getWeight()
    {
        return 10;
    }

    @Override
    public boolean isSpecial()
    {
        return false;
    }
}
