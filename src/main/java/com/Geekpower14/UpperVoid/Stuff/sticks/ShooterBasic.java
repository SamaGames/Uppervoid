package com.geekpower14.uppervoid.stuff.sticks;

import com.geekpower14.uppervoid.stuff.Stuff;
import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShooterBasic extends Stuff
{
	public ShooterBasic(Uppervoid plugin, String name, ItemStack stack, String display, int amount, long reloadTime, boolean glow)
    {
		super(plugin, name, stack, display, "Un lance", amount, reloadTime, glow);
	}

	@Override
	public void use(ArenaPlayer arenaPlayer)
	{
		Player player = arenaPlayer.getPlayerIfOnline();

		if (!this.canUse() || !this.plugin.getArena().getBlockManager().isActive())
			return;

		Item tnt = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.TNT));
        tnt.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));

        this.plugin.getArena().getItemChecker().addItem(tnt, this);

        this.setReloading();

		this.plugin.getArena().increaseStat(player.getUniqueId(), "tntlaunch", 1);
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
				"01110",
				"12221",
				"12321",
				"12221",
				"01110"
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
        ItemMeta meta = base.getItemMeta();

        List<String> lores = meta.getLore();
        lores.add("");
        lores.add(ChatColor.GRAY + "se recharge en " + ChatColor.GOLD + reloadTime/20L + ChatColor.GRAY  + ".");

        meta.setLore(lores);
        base.setItemMeta(meta);

        return base;
    }
}
