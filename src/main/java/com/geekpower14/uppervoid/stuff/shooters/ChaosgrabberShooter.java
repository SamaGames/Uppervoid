package com.geekpower14.uppervoid.stuff.shooters;

import com.geekpower14.uppervoid.Uppervoid;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChaosgrabberShooter extends Shooter
{
    public ChaosgrabberShooter(Uppervoid plugin)
    {
        super(plugin, "chaosgrabber", new ItemStack(Material.BONE, 1), ChatColor.BLUE + "Chaosgrabber" + ChatColor.GRAY + "(Clique-Droit)", 1, (long) (1.7 * 20), true);
    }
}
