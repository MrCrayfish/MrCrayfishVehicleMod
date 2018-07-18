package com.mrcrayfish.vehicle.entity;

import net.minecraft.inventory.IInventory;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IChest
{
    @Nullable
    IInventory getChest();
}
