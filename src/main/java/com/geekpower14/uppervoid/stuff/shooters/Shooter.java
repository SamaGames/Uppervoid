package com.geekpower14.uppervoid.stuff.shooters;

import com.geekpower14.uppervoid.arena.ArenaStatisticsHelper;
import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import com.geekpower14.uppervoid.utils.TNTExplosion;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
class Shooter extends Stuff
{
    Shooter(Uppervoid plugin, int id, ItemStack stack, String display, int amount, long reloadTime, boolean glow)
    {
        super(plugin, id, stack, display, "Votre magnificate canon à TNT", amount, reloadTime, glow);
    }

    @Override
    public void use(ArenaPlayer arenaPlayer)
    {
        Player player = arenaPlayer.getPlayerIfOnline();

        if (!this.canUse(true) || !this.plugin.getArena().getBlockManager().isActive())
            return;

        this.setReloading();

        Item tnt = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.TNT));
        tnt.setMetadata("uv-owner", new FixedMetadataValue(this.plugin, arenaPlayer.getUUID().toString()));
        tnt.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
        tnt.setPickupDelay(Integer.MAX_VALUE);

        this.plugin.getArena().getItemChecker().addItem(tnt, this);

        ((ArenaStatisticsHelper) SamaGamesAPI.get().getGameManager().getGameStatisticsHelper()).increaseTntLaunched(arenaPlayer.getUUID());
    }

    @Override
    public void onItemTouchGround(Arena arena, Item item)
    {
        Location center = item.getLocation();
        UUID launcher = UUID.fromString(item.getMetadata("uv-owner").get(0).asString());
        Block real = center.add(0, -0.5, 0).getBlock();
        World world = center.getWorld();

        List<Block> levelOne = new ArrayList<>();
        List<Block> levelTwo = new ArrayList<>();
        List<Block> levelThree = new ArrayList<>();

        String[] schema = new String[] {
                "01110",
                "12221",
                "12321",
                "12221",
                "01110"
        };

        int middle = (schema.length - 1) / 2;

        int refX = real.getX() - middle;
        int refY = real.getY();
        int refZ = real.getZ() - middle;

        int incrX;
        int incrZ = 0;

        for (String str : schema)
        {
            incrX = 0;

            for (int i = 0; i < str.length(); i++)
            {
                char c = str.charAt(i);

                if (c == '1')
                    levelOne.add(world.getBlockAt(refX + incrX, refY, refZ + incrZ));

                if (c == '2')
                    levelTwo.add(world.getBlockAt(refX + incrX, refY, refZ + incrZ));

                if (c == '3')
                    levelThree.add(world.getBlockAt(refX + incrX, refY, refZ + incrZ));

                incrX++;
            }

            incrZ++;
        }

        for (Block block : levelOne)
            arena.getBlockManager().damage(launcher, block, 1);

        for (Block block : levelTwo)
            arena.getBlockManager().damage(launcher, block, 2);

        for (Block block : levelThree)
            arena.getBlockManager().damage(launcher, block, 3);

        new TNTExplosion(((CraftWorld) center.getWorld()).getHandle(), null, center.getX(), center.getY(), center.getZ(), 3F, false, false).explode();
    }

    @Override
    public ItemStack getItem(ItemStack base)
    {
        ItemMeta meta = base.getItemMeta();

        List<String> lores = meta.getLore();
        lores.add("");
        lores.add(ChatColor.GRAY + "Se recharge en " + ChatColor.GOLD + this.reloadTime / 20L + ChatColor.GRAY  + " secondes .");

        meta.setLore(lores);
        base.setItemMeta(meta);

        return base;
    }
}
