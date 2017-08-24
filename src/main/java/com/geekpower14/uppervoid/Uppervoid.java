package com.geekpower14.uppervoid;

import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.arena.ArenaStatisticsHelper;
import com.geekpower14.uppervoid.listener.PlayerListener;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
public class Uppervoid extends JavaPlugin
{
    private Arena arena;

    @Override
    public void onEnable()
    {
        this.arena = new Arena(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this, this.arena), this);

        SamaGamesAPI.get().getGameManager().setGameStatisticsHelper(new ArenaStatisticsHelper());
        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
        SamaGamesAPI.get().getStatsManager().setStatsToLoad(GamesNames.UPPERVOID, true);
        SamaGamesAPI.get().getShopsManager().setShopToLoad(GamesNames.UPPERVOID, true);
        SamaGamesAPI.get().getGameManager().setKeepPlayerCache(true);
    }

    public Arena getArena()
    {
        return this.arena;
    }
}
