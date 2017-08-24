package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.stuff.grenada.Grenada;
import com.geekpower14.uppervoid.stuff.shooters.BladespinnerShooter;
import com.geekpower14.uppervoid.stuff.shooters.ChaosgrabberShooter;
import com.geekpower14.uppervoid.stuff.shooters.LowShooter;
import com.geekpower14.uppervoid.Uppervoid;

import java.util.ArrayList;
import java.util.List;

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

        //All numbers of grenada possibles
        this.stuffs.add(new Grenada(plugin, 69, 1));
        this.stuffs.add(new Grenada(plugin, 70, 2));
        this.stuffs.add(new Grenada(plugin, 71, 3));
        this.stuffs.add(new Grenada(plugin, 72, 4));
        this.stuffs.add(new Grenada(plugin, 73, 5));
        this.stuffs.add(new Grenada(plugin, 74, 6));

        //All numbers of grapin possibles
        this.stuffs.add(new GrapplingHook(plugin, 75, 1));
        this.stuffs.add(new GrapplingHook(plugin, 76, 2));
        this.stuffs.add(new GrapplingHook(plugin, 77, 3));
        this.stuffs.add(new GrapplingHook(plugin, 78, 4));
        this.stuffs.add(new GrapplingHook(plugin, 79, 5));
    }

    public Stuff getItemByID(int id)
    {
        for(Stuff stuff : this.stuffs)
            if(stuff.getId() == id)
                return stuff.clone();

        this.plugin.getLogger().severe("Stuff not found: " + id);

        return this.getItemByID(66);
    }

}
