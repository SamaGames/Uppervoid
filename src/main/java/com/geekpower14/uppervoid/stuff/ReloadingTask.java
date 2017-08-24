package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.ArenaPlayer;
import org.bukkit.entity.Player;

/*
 * This file is part of Uppervoid.
 *
 * Uppervoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uppervoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Uppervoid.  If not, see <http://www.gnu.org/licenses/>.
 */
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
