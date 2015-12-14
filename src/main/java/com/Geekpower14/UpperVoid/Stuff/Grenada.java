package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.tools.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Grenada extends Stuff
{
    public Grenada(Uppervoid plugin)
    {
		super(plugin, "grenade", new ItemStack(Material.CLAY_BALL, 1), ChatColor.RED + "Grenada " + ChatColor.GRAY + "(Clique-Droit)", "Greeenaaadaa!", 2, 10L, false);
	}

    @Override
    public void use(ArenaPlayer arenaPlayer)
    {
        Player player = arenaPlayer.getPlayerIfOnline();
        ItemStack stack = player.getInventory().getItemInHand();

        if (stack == null || !this.canUse() || !this.plugin.getArena().getBlockManager().isActive() || stack.getAmount() <= 0)
            return;

		this.setReloading();

        player.getWorld().playSound(player.getLocation(), Sound.STEP_SNOW, 3F, 2.0F);

        Item tnt = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CLAY_BALL));
        tnt.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1.5));
        tnt.setPickupDelay(Integer.MAX_VALUE);

        new BukkitRunnable()
        {
            public double time = 1.3D;

            @Override
            public void run()
            {
                if(tnt == null || tnt.isDead())
                {
                    this.cancel();
                    return;
                }

                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                    if(time % 2 == 0)
                        p.getWorld().playSound(tnt.getLocation(), Sound.NOTE_STICKS, 0.8F, 1.5F);
                    else
                        p.getWorld().playSound(tnt.getLocation(), Sound.NOTE_STICKS, 0.8F, 0.5F);
                }

				if(tnt != null && tnt.isOnGround())
					ParticleEffect.FIREWORKS_SPARK.display(1F, 2F, 1F, 0.00005F, 5, tnt.getLocation(), 50);

                if(this.time <= 0)
                {
                    this.cancel();
                    Bukkit.getScheduler().runTask(plugin, () -> onItemTouchGround(plugin.getArena(), tnt));

                    return;
                }

                this.time -= 0.25;
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, 5L);

		this.setUses(this.getUses() - 1);

        stack.setAmount(stack.getAmount() - 1);
        player.getInventory().setItemInHand(stack);

		arenaPlayer.giveStuff();
    }

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

		int ref_x = real.getX() - middle;
		int ref_y = real.getY();
		int ref_z = real.getZ() - middle;

		int incr_x;
		int incr_z = 0;

		for (String str : schema)
        {
			incr_x = 0;

			for (int i = 0; i < str.length(); i++)
            {
				char c = str.charAt(i);

				if (c == '1')
                    levelOne.add(world.getBlockAt(ref_x + incr_x, ref_y, ref_z + incr_z));

				if (c == '2')
                    levelTwo.add(world.getBlockAt(ref_x + incr_x, ref_y, ref_z + incr_z));

				if (c == '3')
                    levelThree.add(world.getBlockAt(ref_x + incr_x, ref_y, ref_z + incr_z));

				incr_x++;
			}

			incr_z++;
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
