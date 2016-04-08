package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import org.bukkit.entity.Player;

public class ReloadingTask implements Runnable
{
    private final Uppervoid plugin;
    private final ArenaPlayer arenaPlayer;
    private final Stuff stuff;

    public ReloadingTask(Uppervoid plugin, ArenaPlayer arenaPlayer, Stuff stuff)
    {
        this.plugin = plugin;
        this.arenaPlayer = arenaPlayer;
        this.stuff = stuff;
    }

    @Override
    public void run()
    {
        this.stuff.setReloading(true);

        final long reloadingTimeStart = System.currentTimeMillis();

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable()
        {
            public boolean activeInHand = stuff.isActiveItem();

            @Override
            public void run()
            {
                Player player = arenaPlayer.getPlayerIfOnline();

                while (true)
                {
                    boolean isActualActiveItem = stuff.isActiveItem();

                    float timePassed = (System.currentTimeMillis() - reloadingTimeStart);
                    float reloadtimeMillis = stuff.getReloadTime() * 50;

                    float prc = timePassed / reloadtimeMillis;

                    if (prc >= 1)
                        break;

                    if (this.activeInHand && !isActualActiveItem)
                    {
                        this.activeInHand = isActualActiveItem;
                        player.setExp(0);

                        continue;
                    }
                    else if (!this.activeInHand && isActualActiveItem)
                    {
                        this.activeInHand = isActualActiveItem;
                    }
                    else if (!this.activeInHand)
                    {
                        continue;
                    }

                    player.setExp(prc);

                }

                stuff.setReloading(false);
            }
        });
    }
}
