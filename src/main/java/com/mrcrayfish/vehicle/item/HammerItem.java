package com.mrcrayfish.vehicle.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;

/**
 * Author: MrCrayfish
 */
public class HammerItem extends SwordItem
{
    public HammerItem(Item.Properties properties)
    {
        super(ItemTier.WOOD, 3, -2.0F, properties);
    }
}
