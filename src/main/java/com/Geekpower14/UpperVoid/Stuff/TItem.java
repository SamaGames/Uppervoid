package com.Geekpower14.UpperVoid.Stuff;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.Arena;
import com.Geekpower14.UpperVoid.UpperVoid;
import com.Geekpower14.UpperVoid.Utils.GlowEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class TItem implements Cloneable {

	public UpperVoid plugin;

	public String name = "Unknown";

	public String alias = "";
	public long reloadTime;
	public int nb = 1;
    public boolean reloading = false;
    protected boolean isGlow = false;
    private APlayer aPlayer;

	public TItem(String name, String display, boolean glow, int nb, long l) {
		this.name = name;
		this.alias = display;
		this.reloadTime = l;
        this.isGlow = glow;

		this.nb = nb;

		plugin = UpperVoid.getPlugin();
	}

	public static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore, boolean glow)
	{
		ItemMeta im = item.getItemMeta();
		if (im == null)
			return item;
		if (name != "")
			im.setDisplayName(name);
		if (lore != null)
			im.setLore(Arrays.asList(lore));
		item.setItemMeta(im);
		if(glow)
			item = GlowEffect.addGlow(item);
		return item;
	}

	public static long secondToTick(double second)
	{
		return (long) (second * 20);
	}

	public abstract ItemStack getItem();

	public String getName()
	{
		return name;
	}

    public boolean isGlow()
    {
        return isGlow;
    }

	public String getDisplayName() {
		return alias;
	}

	public Boolean istheSame(ItemStack it) {
		ItemStack item = this.getItem();

		ItemMeta meta = item.getItemMeta();
		ItemMeta met = it.getItemMeta();

		if (meta == null && met == null) {
			return true;
		}

		if (meta == null || met == null) {
            return false;
        }

		if (!meta.getDisplayName().equalsIgnoreCase(met.getDisplayName())) {
			return false;
		}

		if (meta.getLore() != null && met.getLore() != null && !meta.getLore().equals(met.getLore())) {
			return false;
		}

		return true;
	}

	public Object clone() {
		Object o = null;
		try {
			// On r�cup�re l'instance � renvoyer par l'appel de la
			// m�thode super.clone()
			o = super.clone();
		} catch (CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous impl�mentons
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}

    public void setReloading(final long ticks)
    {
        reloading = true;

        final long reloadingTimeStart = System.currentTimeMillis();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            public boolean activeInHand = isActiveItem();

            @Override
            public void run(){
                Player p = aPlayer.getP();
                while (true) {
                    boolean isActualActiveItem = isActiveItem();

                    float timePassed = (System.currentTimeMillis() - reloadingTimeStart);
                    float reloadtimeMillis = reloadTime * 50;

                    float prc = timePassed / reloadtimeMillis;

                    if (prc >= 1)
                        break;

                    if (activeInHand && !isActualActiveItem) {
                        activeInHand = isActualActiveItem;
                        p.setExp(0);
                        continue;
                    } else if (!activeInHand && isActualActiveItem) {
                        activeInHand = isActualActiveItem;
                    } else if (!activeInHand) {
                        continue;
                    }
                    p.setExp(prc);

                }
                reloading = false;
            }
        });
        return;
    }

    public boolean isActiveItem()
    {
        if(aPlayer == null)
            return false;

        return istheSame(aPlayer.getP().getItemInHand());
    }

    public float getincr(Long time) {
        float result = 0;

        float temp = time;

        result = (100 / (temp / 2)) / 100;

        return result;
    }

	public int getNB() {
		return nb;
	}

	public void setNB(int nb) {
		this.nb = nb;
	}

	public abstract void rightAction(APlayer ap, APlayer.ItemSLot slot);

	public abstract void leftAction(APlayer p, APlayer.ItemSLot slot);

	public abstract void onItemTouchGround(Arena arena, Item item);

    public boolean canUse()
    {
        return !(aPlayer.isSpectator() ||aPlayer.isReloading() || reloading);
    }

    public APlayer getAPlayer() {
        return aPlayer;
    }

    public void setaPlayer(APlayer aPlayer) {
        this.aPlayer = aPlayer;
    }
}
