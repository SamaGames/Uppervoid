package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *                )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 *
 * Created by Jérémy L. (BlueSlime) on 09/04/2017
 */
public class InvisibilityPowerup extends UppervoidPowerup
{
    public InvisibilityPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 8, 0));
    }

    @Override
    public String getName()
    {
        return ChatColor.WHITE + "Invisibilité : 8 secondes";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.QUARTZ_BLOCK, 1);
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
