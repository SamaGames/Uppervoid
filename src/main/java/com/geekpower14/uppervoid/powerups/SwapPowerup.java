package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.tools.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SwapPowerup extends UppervoidPowerup
{
    private final Random random;

    public SwapPowerup(Uppervoid plugin, Arena arena)
    {
        super(plugin, arena);

        this.random = new Random();
    }

    @Override
    public void onPickup(Player player)
    {
        List<ArenaPlayer> players = this.arena.getInGamePlayers().values().stream().filter(arenaPlayer -> !arenaPlayer.getUUID().equals(player.getUniqueId())).collect(Collectors.toList());
        Player randomized = players.get(this.random.nextInt(players.size())).getPlayerIfOnline();

        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        randomized.playSound(randomized.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);

        ParticleEffect.PORTAL.display(0.5F, 0.5F, 0.5F, 1.0F, 5, player.getLocation(), 150.0D);
        ParticleEffect.PORTAL.display(0.5F, 0.5F, 0.5F, 1.0F, 5, randomized.getLocation(), 150.0D);

        Location back = player.getLocation();
        player.teleport(randomized.getLocation());
        randomized.teleport(back);
    }

    @Override
    public String getName()
    {
        return ChatColor.DARK_AQUA + "Echange de position";
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(Material.EYE_OF_ENDER, 1);
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
