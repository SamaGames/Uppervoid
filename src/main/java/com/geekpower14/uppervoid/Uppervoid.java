package com.geekpower14.uppervoid;

import com.geekpower14.uppervoid.arena.Arena;
import com.geekpower14.uppervoid.listener.PlayerListener;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.api.stats.IStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Uppervoid extends JavaPlugin
{
    private Arena arena;

    @Override
    public void onEnable()
    {
        this.arena = new Arena(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this, this.arena), this);
        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
        SamaGamesAPI.get().getStatsManager().setStatsToLoad(GamesNames.UPPERVOID, true);
    }

    public Arena getArena()
    {
        return this.arena;
    }
}
