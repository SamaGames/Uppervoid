package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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

        Player player = this.arenaPlayer.getPlayerIfOnline();
        player.setExp(0);

        BukkitTask infoxp = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
        {
            float xp = player.getExp();
            xp += this.getCooldown();

            if (xp >= 1)
                xp = 1;

            player.setExp(xp);
        }, 0L, 2L);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            this.stuff.setReloading(false);
            player.setExp(1);
            infoxp.cancel();
        }, this.stuff.getReloadTime());
    }

    private float getCooldown()
    {
        return (100 / (this.stuff.getReloadTime() / 2)) / 100;
    }
}
