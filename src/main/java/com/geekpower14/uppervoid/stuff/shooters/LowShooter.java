package com.geekpower14.uppervoid.stuff.shooters;

import com.geekpower14.uppervoid.Uppervoid;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LowShooter extends Shooter
{
    public LowShooter(Uppervoid plugin)
    {
        super(plugin, "shooter", new ItemStack(Material.STICK, 1), "Shooter", 1, 20L * 2, false);
    }
}
