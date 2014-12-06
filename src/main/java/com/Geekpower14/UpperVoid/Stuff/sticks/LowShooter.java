package com.Geekpower14.UpperVoid.Stuff.sticks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Geekpower14 on 04/10/2014.
 */
public class LowShooter extends ShooterBasic{
    public LowShooter() {
        super("shooter", ChatColor.GOLD + "Shooter " + ChatColor.GRAY
                + "(Clique-Droit)", false, 1, secondToTick(2));
    }

    public ItemStack getItem() {
        ItemStack coucou = setItemNameAndLore(new ItemStack(Material.STICK),
                ChatColor.GOLD + "Shooter " + ChatColor.GRAY+ "(Clique-Droit)",
                new String[]{
                    ChatColor.DARK_GRAY + "Recharge en " + ChatColor.GOLD + reloadTime/20L + ChatColor.DARK_GRAY + " secondes."
                },
                false);

        return coucou;
    }
}
