package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import net.samagames.tools.GlowEffect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public abstract class Stuff implements Cloneable
{
    protected final Uppervoid plugin;
    protected final int id;
    protected final ItemStack stack;
    protected final String display;
    protected final String lore;
    protected final long reloadTime;
    protected final boolean isGlow;

    protected ArenaPlayer arenaPlayer;
    protected int uses;
    protected boolean reloading = false;

    public Stuff(Uppervoid plugin, int id, ItemStack stack, String display, String lore, int uses, long reloadTime, boolean glow)
    {
        this.plugin = plugin;
        this.id = id;
        this.stack = stack;
        this.display = ChatColor.GOLD + display + ChatColor.GRAY + " (Clique-Droit)";
        this.lore = lore;
        this.uses = uses;
        this.reloadTime = reloadTime;
        this.isGlow = glow;
    }

    public abstract void use(ArenaPlayer arenaPlayer);
    public abstract ItemStack getItem(ItemStack base);

    public void onItemTouchGround(Arena arena, Item item) {}

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
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new ReloadingTask(this.plugin, this.arenaPlayer, this));
    }

    public void setReloading(boolean reloading)
    {
        this.reloading = reloading;
    }

    public ItemStack getItem()
    {
        ItemStack modifiedStack = this.stack.clone();
        modifiedStack.setAmount(this.uses);

        ItemMeta meta = modifiedStack.getItemMeta();

        meta.setDisplayName(this.display);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + this.lore));

        modifiedStack.setItemMeta(meta);

        if (this.isGlow)
            GlowEffect.addGlow(modifiedStack);

        return this.getItem(modifiedStack);
    }

    public int getId()
    {
        return this.id;
    }

    public int getUses()
    {
        return this.uses;
    }

    public long getReloadTime()
    {
        return this.reloadTime;
    }

    public boolean canUse()
    {
        return this.arenaPlayer != null && !(this.arenaPlayer.isSpectator() || this.arenaPlayer.isReloading() || this.reloading);
    }

    @SuppressWarnings("deprecation")
    public boolean isActiveItem()
    {
        if (this.arenaPlayer == null)
            return false;
        ItemStack itemStack = this.getItem();
        return itemStack != null && itemStack.isSimilar(this.arenaPlayer.getPlayerIfOnline().getItemInHand());
    }

    public Stuff clone()
    {
        try {
            return (Stuff) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
