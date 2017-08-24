package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.entity.Item;

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
public class ItemChecker implements Runnable
{
    private final Uppervoid plugin;
    private final ArrayList<Stock> items;

    public ItemChecker(Uppervoid plugin)
    {
        this.plugin = plugin;
        this.items = new ArrayList<>();
    }

    @Override
    public void run()
    {
        List<Stock> toRemove = new ArrayList<>();

        for (Stock stock : this.items)
        {
            if (stock == null || stock.getItem() == null || stock.getItem().isDead())
            {
                toRemove.add(stock);
                continue;
            }

            if (stock.getItem().isOnGround())
            {
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.finish(stock));
            }
        }

        toRemove.forEach(this.items::remove);
    }

    public void addItem(Item item, Stuff stuff)
    {
        this.items.add(new Stock(item, stuff));
    }

    public void finish(Stock stock)
    {
        Arena arena = this.plugin.getArena();
        Item item = stock.getItem();

        stock.getStuff().onItemTouchGround(arena, item);

        this.items.remove(stock);

        item.remove();
    }

    public class Stock
    {
        private final Item item;
        private final Stuff stuff;

        public Stock(Item item, Stuff stuff)
        {
            this.item = item;
            this.stuff = stuff;
        }

        public Item getItem()
        {
            return this.item;
        }

        public Stuff getStuff()
        {
            return this.stuff;
        }
    }
}
