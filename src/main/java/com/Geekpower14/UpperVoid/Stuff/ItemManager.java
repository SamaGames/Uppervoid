package com.Geekpower14.UpperVoid.Stuff;

import com.Geekpower14.UpperVoid.Stuff.grapin.GrapinBasic;
import com.Geekpower14.UpperVoid.Stuff.grenade.Grenada;
import com.Geekpower14.UpperVoid.Stuff.sticks.LowAdvancedShooter;
import com.Geekpower14.UpperVoid.Stuff.sticks.LowShooter;
import com.Geekpower14.UpperVoid.UpperVoid;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

	public UpperVoid plugin;

	public List<TItem> stuff = new ArrayList<TItem>();

	public ItemManager(UpperVoid pl) {
		plugin = pl;

        stuff.add(new LowShooter());
        stuff.add(new LowAdvancedShooter());
        stuff.add(new Grenada());
        stuff.add(new GrapinBasic("grapin", "grapin", true, 2, 0L));
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
