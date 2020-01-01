package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends Inventory
{
    private IStorage wrapper;

    public StorageInventory(IStorage wrapper, int size)
    {
        super(size);
        this.wrapper = wrapper;
    }

    public boolean isStorageItem(ItemStack stack)
    {
        return this.wrapper.isStorageItem(stack);
    }
}
