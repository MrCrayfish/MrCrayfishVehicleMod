package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public interface IAttachableChest extends IStorage
{
    boolean hasChest();

    void attachChest(ItemStack stack);

    void removeChest();
}
