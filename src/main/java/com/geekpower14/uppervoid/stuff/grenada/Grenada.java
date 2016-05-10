package com.geekpower14.uppervoid.stuff.grenada;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import com.geekpower14.uppervoid.stuff.Stuff;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Grenada extends Stuff
{
    public Grenada(Uppervoid plugin, int id, int uses)
    {
        super(plugin, id, new ItemStack(Material.CLAY_BALL, 1), ChatColor.RED + "Grenada", "Greeenaaadaa!", 2, 10L, false);
        this.uses = uses;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void use(ArenaPlayer arenaPlayer)
    {
        Player player = arenaPlayer.getPlayerIfOnline();
        ItemStack stack = player.getInventory().getItemInHand();

        if (stack == null || !this.canUse() || !this.plugin.getArena().getBlockManager().isActive() || stack.getAmount() <= 0)
            return;

        this.setReloading();

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 3F, 2.0F);

        Item tnt = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CLAY_BALL));
        tnt.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1.5));
        tnt.setPickupDelay(Integer.MAX_VALUE);

        new GrenadaExplosionTask(this.plugin, this, tnt).runTaskTimerAsynchronously(this.plugin, 0L, 5L);

        this.setUses(this.getUses() - 1);

        stack.setAmount(stack.getAmount() - 1);
        player.getInventory().setItemInHand(stack);

        arenaPlayer.giveStuff();
    }

    @Override
    public void onItemTouchGround(Arena arena, Item item)
    {
        Location center = item.getLocation();
        Block real = center.add(0, -0.5, 0).getBlock();
        World world = center.getWorld();

        ArrayList<Block> levelOne = new ArrayList<>();
        ArrayList<Block> levelTwo = new ArrayList<>();
        ArrayList<Block> levelThree = new ArrayList<>();

        String[] schema = new String[] {
                "00011111000",
                "00111111100",
                "01112221110",
                "11122222111",
                "11222322211",
                "11223332211",
                "11222322211",
                "11122222111",
                "01112221110",
                "00111111100",
                "00011111000"
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
            arena.getBlockManager().damage(block, 1);

        for (Block block : levelTwo)
            arena.getBlockManager().damage(block, 2);

        for (Block block : levelThree)
            arena.getBlockManager().damage(block, 3);

        center.getWorld().createExplosion(center.getX(), center.getY(), center.getZ(), 2.5F, false, false);
    }

    @Override
    public ItemStack getItem(ItemStack base)
    {
        return base;
    }
}
