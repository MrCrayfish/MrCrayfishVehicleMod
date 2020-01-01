package com.mrcrayfish.vehicle.item;

import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class PartItem extends Item
{
    private boolean colored;

    public PartItem(String id, Item.Properties properties)
    {
        super(properties);
        this.setRegistryName(id);
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
