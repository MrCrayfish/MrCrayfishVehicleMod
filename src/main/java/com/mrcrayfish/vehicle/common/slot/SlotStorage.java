package com.mrcrayfish.vehicle.common.slot;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class SlotStorage extends Slot
{
    private StorageInventory storageInventory;

    public SlotStorage(StorageInventory storageInventory, int index, int xPosition, int yPosition)
    {
        super(storageInventory, index, xPosition, yPosition);
        this.storageInventory = storageInventory;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return storageInventory.isStorageItem(stack);
    }
}
