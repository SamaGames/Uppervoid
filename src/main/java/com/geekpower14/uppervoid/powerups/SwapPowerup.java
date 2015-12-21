package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwapPowerup extends UppervoidPowerup
{
    public SwapPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

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
