package com.geekpower14.uppervoid.powerups;

import net.samagames.tools.powerups.Powerup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwapPowerup implements Powerup
{
    @Override
    public void onPickup(Player player)
    {

    }

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public ItemStack getIcon()
    {
        return null;
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
