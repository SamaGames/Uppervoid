package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.tools.ParticleEffect;
import net.samagames.tools.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * This file is part of Uppervoid.
 *
 * Uppervoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uppervoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Uppervoid.  If not, see <http://www.gnu.org/licenses/>.
 */
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

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        randomized.playSound(randomized.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

        ParticleEffect.PORTAL.display(0.5F, 0.5F, 0.5F, 1.0F, 5, player.getLocation(), 120.0D);
        ParticleEffect.PORTAL.display(0.5F, 0.5F, 0.5F, 1.0F, 5, randomized.getLocation(), 120.0D);

        Location back = player.getLocation();
        player.teleport(randomized.getLocation());
        randomized.teleport(back);

        this.arena.getCoherenceMachine().getMessageManager().writeCustomMessage(ChatColor.RED + PlayerUtils.getColoredFormattedPlayerName(player) + ChatColor.YELLOW + " a échangé sa position avec " + ChatColor.RED + PlayerUtils.getColoredFormattedPlayerName(randomized) + ChatColor.YELLOW + ".", true);
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
