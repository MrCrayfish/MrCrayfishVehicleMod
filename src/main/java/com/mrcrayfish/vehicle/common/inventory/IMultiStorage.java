package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public interface IMultiStorage
{
    Map<String, IStorage> getStorageInventories();

    @Nullable
    default IStorage getStorageInventory(String key)
    {
        return this.getStorageInventories().get(key);
    }

    default void readInventories(CompoundNBT tag)
    {
        CompoundNBT storageTag = tag.getCompound("Storage");
        this.getStorageInventories().forEach((key, storage) -> {
            CompoundNBT inventory = storageTag.getCompound(key);
            InventoryUtil.readInventoryToNBT(inventory, key, storage);
        });
    }

    default void writeInventories(CompoundNBT tag)
    {
        CompoundNBT storageTag = new CompoundNBT();
        this.getStorageInventories().forEach((key, storage) -> {
            InventoryUtil.writeInventoryToNBT(storageTag, key, storage);
        });
        tag.put("Storage", storageTag);
    }
}
