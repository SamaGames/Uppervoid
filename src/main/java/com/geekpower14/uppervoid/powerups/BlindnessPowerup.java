package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BlindnessPowerup extends UppervoidPowerup
{
    public BlindnessPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);
    }

    @Override
    public void onPickup(Player player)
    {
        List<Squid> squids = new ArrayList<>();

        for (ArenaPlayer gamePlayer : this.arena.getInGamePlayers().values())
        {
            if (gamePlayer.getPlayerIfOnline() == null || gamePlayer.getUUID().equals(player.getUniqueId()))
                continue;

            gamePlayer.getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 0));
            gamePlayer.getPlayerIfOnline().playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0F, 1.0F);

            Squid squid = gamePlayer.getPlayerIfOnline().getWorld().spawn(gamePlayer.getPlayerIfOnline().getLocation(), Squid.class);
            gamePlayer.getPlayerIfOnline().setPassenger(squid);

            squids.add(squid);
        }

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            squids.forEach(Squid::remove);
            squids.clear();
        }, 20L);
    }

    @Override
    public String getName()
    {
        return ChatColor.DARK_GRAY + "Ninja Poulpe";
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
