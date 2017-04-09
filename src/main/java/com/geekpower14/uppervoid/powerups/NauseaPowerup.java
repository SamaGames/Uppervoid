package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
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
public class NauseaPowerup extends UppervoidPowerup
{
    public NauseaPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        for (ArenaPlayer gamePlayer : this.arena.getInGamePlayers().values())
        {
            if (gamePlayer.getPlayerIfOnline() == null || gamePlayer.getUUID().equals(player.getUniqueId()))
                continue;

            gamePlayer.getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 2));
        }
    }

    @Override
    public String getName()
    {
        return ChatColor.DARK_GREEN + "Nausée";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.ROTTEN_FLESH, 1);
    }

    @Override
    public double getWeight()
    {
        return 10;
    }

    @Override
    public boolean isSpecial()
    {
        return false;
    }
}
