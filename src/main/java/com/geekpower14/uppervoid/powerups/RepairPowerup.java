package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *                )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 *
 * Created by Jérémy L. (BlueSlime) on 09/04/2017
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
        return 40;
    }

    @Override
    public boolean isSpecial()
    {
        return false;
    }
}
