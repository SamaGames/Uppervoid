package com.geekpower14.uppervoid.stuff;

import com.geekpower14.uppervoid.Uppervoid;
import com.geekpower14.uppervoid.arena.Arena;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;

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
