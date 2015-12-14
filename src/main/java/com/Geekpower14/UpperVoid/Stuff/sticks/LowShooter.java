package com.geekpower14.uppervoid.stuff.sticks;

import com.geekpower14.uppervoid.Uppervoid;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LowShooter extends ShooterBasic
{
    public LowShooter(Uppervoid plugin)
    {
        super(plugin, "shooter", new ItemStack(Material.STICK, 1), ChatColor.GOLD + "Shooter " + ChatColor.GRAY + "(Clique-Droit)", 1, 20L * 2, false);
    }
}
