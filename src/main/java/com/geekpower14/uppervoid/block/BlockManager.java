package com.geekpower14.uppervoid.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockManager
{
    private final List<BlockGroup> groups;
    private boolean active = true;

    public BlockManager()
    {
        this.groups = new ArrayList<>();
        this.loadGroups();
    }

    public boolean damage(UUID damager, Block block)
    {
        return this.damage(damager, block, 1);
    }

    public boolean damage(UUID damager, Block block, int damage)
    {
        if (!this.active)
            return false;

        if (block.getRelative(BlockFace.DOWN).getType() != Material.QUARTZ_BLOCK)
            return false;

        BlockGroup blockGroup = this.getBlockGroup(block);

        return blockGroup != null && blockGroup.damage(damager, block, damage);
    }

    public boolean repair(UUID damager, Block block)
    {
        if (!this.active)
            return false;

        if (block.getRelative(BlockFace.DOWN).getType() != Material.QUARTZ_BLOCK)
            return false;

        BlockGroup blockGroup = this.getBlockGroup(block);

        return blockGroup != null && blockGroup.repair(damager, block);
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public BlockGroup getBlockGroup(Block block)
    {
        for (BlockGroup blockGroup : this.groups)
            if (blockGroup.isThis(block))
                return blockGroup;

        return null;
    }

    public boolean isActive()
    {
        return this.active;
    }

    private void loadGroups()
    {
        JsonArray defaultBlockGroup = new JsonArray();
        defaultBlockGroup.add(new JsonPrimitive("GRASS, 0"));
        defaultBlockGroup.add(new JsonPrimitive("DIRT, 1"));
        defaultBlockGroup.add(new JsonPrimitive("DIRT, 2"));

        JsonArray defaultBlockGroups = new JsonArray();
        defaultBlockGroups.add(defaultBlockGroup);

        JsonArray blockGroups = SamaGamesAPI.get().getGameManager().getGameProperties().getOption("blocks", defaultBlockGroups).getAsJsonArray();

        for(JsonElement data : blockGroups)
            this.groups.add(new BlockGroup(data.getAsJsonArray()));
    }
}
