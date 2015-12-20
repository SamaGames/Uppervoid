package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.arena.ArenaPlayer;
import org.bukkit.entity.Player;

public class ReloadingTask implements Runnable
{
    private final ArenaPlayer arenaPlayer;
    private final Stuff stuff;
    private final long initialTime;

    private boolean activeInHand;

    public ReloadingTask(ArenaPlayer arenaPlayer, Stuff stuff)
    {
        this.arenaPlayer = arenaPlayer;
        this.stuff = stuff;
        this.initialTime = System.currentTimeMillis();

        this.activeInHand = stuff.isActiveItem();
    }

    @Override
    public void run()
    {
        this.stuff.setReloading(true);

        Player player = this.arenaPlayer.getPlayerIfOnline();

        while (true)
        {
            boolean isActualActiveItem = this.stuff.isActiveItem();

            float timePassed = System.currentTimeMillis() - this.initialTime;
            float reloadTimeMillis = this.stuff.reloadTime * 50;

            float prc = timePassed / reloadTimeMillis;

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
                if (isActualActiveItem)
                    this.activeInHand = isActualActiveItem;
                else
                    continue;
            }

            player.setExp(prc);
        }

        this.stuff.setReloading(false);
    }
}
