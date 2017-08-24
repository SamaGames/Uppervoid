package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
public class RepairPowerup extends UppervoidPowerup
{
    public RepairPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        player.sendMessage(ChatColor.RED + "Vous réparez maintenant les blocs sur lesquels vous marchez !");

        this.arena.addBuilder(player);
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> this.arena.removeBuilder(player), 20L * 10);
    }

    @Override
    public String getName()
    {
        return ChatColor.RED + "Réparation du sol";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.BRICK, 1);
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
