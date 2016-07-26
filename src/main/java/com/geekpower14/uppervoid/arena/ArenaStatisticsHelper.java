package com.geekpower14.uppervoid.arena;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameStatisticsHelper;

import java.util.UUID;

public class ArenaStatisticsHelper implements IGameStatisticsHelper
{
    @Override
    public void increasePlayedTime(UUID uuid, long playedTime)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByPlayedTime(playedTime);
    }

    @Override
    public void increasePlayedGames(UUID uuid)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByPlayedGames(1);
    }

    @Override
    public void increaseWins(UUID uuid)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByWins(1);
    }

    public void increaseBlocks(UUID uuid)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByBlocks(1);
    }

    public void increaseGrenades(UUID uuid)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByGrenades(1);
    }

    public void increaseTntLaunched(UUID uuid)
    {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getUppervoidStatistics().incrByTntLaunched(1);
    }
}
