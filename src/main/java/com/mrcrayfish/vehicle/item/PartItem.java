package com.mrcrayfish.vehicle.item;

import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class PartItem extends Item
{
    private boolean colored;

    public PartItem(Item.Properties properties)
    {
        super(properties);
    }

    public PartItem setColored()
    {
        this.colored = true;
        return this;
    }

    public boolean isColored()
    {
        return colored;
    }
}
