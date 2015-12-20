package com.geekpower14.uppervoid.powerups;

import net.md_5.bungee.api.ChatColor;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindnessPowerup implements Powerup
{
    @Override
    public void onPickup(Player player)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 0));
        player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0F, 1.0F);
    }

    @Override
    public String getName()
    {
        return ChatColor.BLACK + "Crachat de poulpe : 2 secondes";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.INK_SACK, 1);
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
