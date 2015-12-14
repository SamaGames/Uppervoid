package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.tools.GlowEffect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public abstract class Stuff implements Cloneable
{
	protected final Uppervoid plugin;
    protected final String name;
    protected final ItemStack stack;
    protected final String display;
    protected final String lore;
    protected final long reloadTime;
    protected final boolean isGlow;

    protected ArenaPlayer arenaPlayer;
    protected int uses;
    protected boolean reloading = false;

	public Stuff(Uppervoid plugin, String name, ItemStack stack, String display, String lore, int uses, long reloadTime, boolean glow)
    {
        this.plugin = plugin;
		this.name = name;
        this.stack = stack;
		this.display = display;
        this.lore = lore;
        this.uses = uses;
		this.reloadTime = reloadTime;
        this.isGlow = glow;
	}

    public abstract void use(ArenaPlayer arenaPlayern);
    public abstract void onItemTouchGround(Arena arena, Item item);
	public abstract ItemStack getItem(ItemStack base);

    public void setOwner(ArenaPlayer arenaPlayer)
    {
        this.arenaPlayer = arenaPlayer;
    }

    public void setUses(int uses)
    {
        this.uses = uses;
    }

    public void setReloading()
    {
        this.reloading = true;

        long reloadingTimeStart = System.currentTimeMillis();

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable()
        {
            public boolean activeInHand = isActiveItem();

            @Override
            public void run()
            {
                Player player = arenaPlayer.getPlayerIfOnline();

                while (true)
                {
                    boolean isActualActiveItem = isActiveItem();

                    float timePassed = (System.currentTimeMillis() - reloadingTimeStart);
                    float reloadtimeMillis = reloadTime * 50;

                    float prc = timePassed / reloadtimeMillis;

                    if (prc >= 1)
                        break;

                    if (activeInHand && !isActualActiveItem)
                    {
                        activeInHand = isActualActiveItem;
                        player.setExp(0);

                        continue;
                    }
                    else if (!activeInHand && isActualActiveItem)
                    {
                        activeInHand = isActualActiveItem;
                    }
                    else if (!activeInHand)
                    {
                        continue;
                    }

                    player.setExp(prc);
                }

                reloading = false;
            }
        });
    }

    public ItemStack getItem()
    {
        ItemStack stack = this.stack.clone();
        stack.setAmount(this.uses);

        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(this.name);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + this.lore));

        if (this.isGlow)
            GlowEffect.addGlow(stack);

        return this.getItem(stack);
    }

    public String getName()
    {
        return this.name;
    }

	public int getUses()
    {
		return this.uses;
	}

    public boolean canUse()
    {
        return !(this.arenaPlayer.isSpectator() || this.arenaPlayer.isReloading() || this.reloading);
    }

    public boolean isActiveItem()
    {
        if(this.arenaPlayer == null)
            return false;

        return this.isSames(this.arenaPlayer.getPlayerIfOnline().getItemInHand());
    }

    public boolean isSames(ItemStack stack)
    {
        ItemStack item = this.getItem();

        ItemMeta oneMeta = item.getItemMeta();
        ItemMeta twoMeta = stack.getItemMeta();

        if (oneMeta == null && twoMeta == null)
            return true;
        else if (oneMeta == null || twoMeta == null)
            return false;
        else if (!oneMeta.getDisplayName().equalsIgnoreCase(twoMeta.getDisplayName()))
            return false;
        else if (oneMeta.getLore() != null && twoMeta.getLore() != null && !oneMeta.getLore().equals(twoMeta.getLore()))
            return false;

        return true;
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
