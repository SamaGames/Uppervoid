package com.geekpower14.uppervoid.powerups;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import net.samagames.tools.powerups.Powerup;

public abstract class UppervoidPowerup implements Powerup
{
    protected final Uppervoid plugin;
    protected final Arena arena;

    public UppervoidPowerup(Uppervoid plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }
}
