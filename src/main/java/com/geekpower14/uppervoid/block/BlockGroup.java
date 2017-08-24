package com.geekpower14.uppervoid.block;

import com.geekpower14.uppervoid.arena.ArenaStatisticsHelper;
import com.google.gson.JsonArray;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.ParticleEffect;
import net.samagames.tools.SimpleBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
public class BlockGroup
{
    private static final SimpleBlock VOID = new SimpleBlock(Material.AIR);

    private final SimpleBlock blockFine;
    private final SimpleBlock blockWarning;
    private final SimpleBlock blockCritical;

    public BlockGroup(JsonArray group)
    {
        String[] blockFineData = group.get(0).getAsString().split(", ");
        this.blockFine = new SimpleBlock(Material.matchMaterial(blockFineData[0]), Integer.valueOf(blockFineData[1]));

        String[] blockWarningData = group.get(1).getAsString().split(", ");
        this.blockWarning = new SimpleBlock(Material.matchMaterial(blockWarningData[0]), Integer.valueOf(blockWarningData[1]));

        String[] blockCriticalData = group.get(2).getAsString().split(", ");
        this.blockCritical = new SimpleBlock(Material.matchMaterial(blockCriticalData[0]), Integer.valueOf(blockCriticalData[1]));
    }

    public boolean isThis(Block block)
    {
        if (block == null)
            return false;

        if (this.is(block, this.blockFine))
            return true;
        else if (this.is(block, this.blockWarning))
            return true;
        else if (this.is(block, this.blockCritical))
            return true;

        return false;
    }

    public boolean damage(UUID damager, Block block, int damage)
    {
        boolean result = false;

        for (int i = 0; i < damage; i++)
            if (this.setNext(damager, block))
                result = true;

        if(result)
        {
            ParticleEffect.VILLAGER_HAPPY.display(0.2F, 0.1F, 0.2F, 10F, 1, block.getLocation().add(0.5D, 1.1D, 0.5D), 50);
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.2F, 0.3F, 0.2F, 10F, 5, block.getLocation().add(0.5D, 1.1D, 0.5D), 50);
        }

        return result;
    }

    public boolean repair(Block block, int damage)
    {
        boolean result = false;

        for (int i = 0; i < damage; i++)
            if (this.setPrevious(block))
                result = true;

        if (result)
        {
            ParticleEffect.VILLAGER_HAPPY.display(0.2F, 0.1F, 0.2F, 10F, 1, block.getLocation().add(0.5D, 1.1D, 0.5D), 50);
            ParticleEffect.HEART.display(0.2F, 0.3F, 0.2F, 10F, 5, block.getLocation().add(0.5D, 1.1D, 0.5D), 50);
        }

        return result;
    }

    private boolean setPrevious(Block block)
    {
        if (block == null)
            return false;

        if (this.is(block, this.blockFine))
        {
            return true;
        }
        else if (this.is(block, this.blockWarning))
        {
            this.set(block, this.blockFine);
            return true;
        }
        else if (this.is(block, this.blockCritical))
        {
            this.set(block, this.blockWarning);
            return true;
        }

        return false;
    }

    private boolean setNext(UUID damager, Block block)
    {
        if (block == null)
            return false;

        if (this.is(block, this.blockFine))
        {
            this.set(block, this.blockWarning);
            return true;
        }
        else if (this.is(block, this.blockWarning))
        {
            this.set(block, this.blockCritical);
            return true;
        }
        else if (this.is(block, this.blockCritical))
        {
            this.set(block, VOID);
            this.set(block.getRelative(BlockFace.DOWN), VOID);

            if (damager != null)
                ((ArenaStatisticsHelper) SamaGamesAPI.get().getGameManager().getGameStatisticsHelper()).increaseBlocks(damager);

            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    private void set(Block block, SimpleBlock simpleBlock)
    {
        block.setType(simpleBlock.getType());
        block.setData(simpleBlock.getData());
    }

    @SuppressWarnings("deprecation")
    private boolean is(Block block, SimpleBlock modal)
    {
        return block.getType() == modal.getType() && block.getData() == modal.getData();
    }
}
