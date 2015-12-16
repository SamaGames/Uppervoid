package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.stuff.grenada.Grenada;
import com.geekpower14.uppervoid.stuff.shooters.BladespinnerShooter;
import com.geekpower14.uppervoid.stuff.shooters.ChaosgrabberShooter;
import com.geekpower14.uppervoid.stuff.shooters.LowShooter;
import com.geekpower14.uppervoid.Uppervoid;

import java.util.ArrayList;
import java.util.List;

public class ItemManager
{
    public final Uppervoid plugin;
    public final List<Stuff> stuffs;

    public ItemManager(Uppervoid plugin)
    {
        this.plugin = plugin;
        this.stuffs = new ArrayList<>();

        this.stuffs.add(new LowShooter(plugin));
        this.stuffs.add(new BladespinnerShooter(plugin));
        this.stuffs.add(new ChaosgrabberShooter(plugin));

        this.stuffs.add(new Grenada(plugin));

        this.stuffs.add(new GrapplingHook(plugin));
    }

    public Stuff getItemByName(String name)
    {
        for(Stuff stuff : this.stuffs)
        {
            if(stuff.getName().equals(name))
                return (Stuff) stuff.clone();
        }

        return getItemByName("shooter");
    }

}
