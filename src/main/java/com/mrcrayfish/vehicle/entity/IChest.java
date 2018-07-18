package com.mrcrayfish.vehicle.entity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IChest
{
    @Nullable
    IInventory getChest();

    boolean hasChest();

    void attachChest(ItemStack stack);
}
