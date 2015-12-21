package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindnessPowerup extends UppervoidPowerup
{
    public BlindnessPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        for (ArenaPlayer gamePlayer : this.arena.getInGamePlayers().values())
        {
            if (gamePlayer == null)
                continue;

            if (gamePlayer.getUUID().equals(player.getUniqueId()))
                continue;

            gamePlayer.getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
            gamePlayer.getPlayerIfOnline().playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0F, 1.0F);
        }
    }

    @Override
    public String getName()
    {
        return ChatColor.BLACK + "Crachat de poulpe : 3 secondes";
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
