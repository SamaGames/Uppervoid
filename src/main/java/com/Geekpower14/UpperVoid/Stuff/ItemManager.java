package com.Geekpower14.UpperVoid.Stuff;

import com.Geekpower14.UpperVoid.Stuff.grapin.Grapin;
import com.Geekpower14.UpperVoid.Stuff.grenade.Grenada;
import com.Geekpower14.UpperVoid.Stuff.sticks.BladespinnerShooter;
import com.Geekpower14.UpperVoid.Stuff.sticks.ChaosgrabberShooter;
import com.Geekpower14.UpperVoid.Stuff.sticks.LowShooter;
import com.Geekpower14.UpperVoid.UpperVoid;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

	public UpperVoid plugin;

	public List<TItem> stuff = new ArrayList<TItem>();

	public ItemManager(UpperVoid pl) {
		plugin = pl;

        //Shooters
        stuff.add(new LowShooter());//2.0sec
        stuff.add(new BladespinnerShooter());//1.7sec
        stuff.add(new ChaosgrabberShooter());//1.5sec
        //Grenades
        stuff.add(new Grenada());
        //Grapins
        stuff.add(new Grapin());
	}

    public TItem getItemByName(String name)
    {
        for(TItem i : stuff)
        {
            if(i.getName().equals(name))
                return (TItem) i.clone();
        }

        return getItemByName("shooter");
    }

}
