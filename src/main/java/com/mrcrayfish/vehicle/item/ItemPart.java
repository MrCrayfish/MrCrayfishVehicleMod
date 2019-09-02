package com.mrcrayfish.vehicle.item;

import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemPart extends Item
{
    private boolean colored = false;

    public ItemPart(String id)
    {
        this.setUnlocalizedName(id.replace(":", "."));
        this.setRegistryName(id);
    }

    public ItemPart setColored()
    {
        this.colored = true;
        return this;
    }

    public boolean isColored()
    {
        return colored;
    }
}
