package com.Geekpower14.UpperVoid.Stuff.sticks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Geekpower14 on 04/10/2014.
 */
public class BladespinnerShooter extends ShooterBasic{
    public BladespinnerShooter() {
        super("bladespinner", ChatColor.GOLD + "Bladespinner" + ChatColor.GRAY
                + "(Clique-Droit)", true, 1, secondToTick(1.7));
    }

    public ItemStack getItem() {
        ItemStack coucou = setItemNameAndLore(new ItemStack(Material.BLAZE_ROD),
                ChatColor.GOLD + "Bladespinner " + ChatColor.GRAY+ "(Clique-Droit)",
                new String[]{
                        ChatColor.DARK_GRAY + "Recharge en " + ChatColor.GOLD + reloadTime/20L + ChatColor.DARK_GRAY + " secondes."
                },
                false);

        return coucou;
    }
}
