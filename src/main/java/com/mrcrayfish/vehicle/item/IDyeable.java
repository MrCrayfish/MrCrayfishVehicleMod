package com.mrcrayfish.vehicle.item;

import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public interface IDyeable
{
    boolean hasColor(ItemStack stack);

    int getColor(ItemStack stack);

    void setColor(ItemStack stack, int color);
}
