package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.util.text.ITextComponent;

/**
 * Author: MrCrayfish
 */
public class BasicStorage implements IStorage
{
    private final ITextComponent title;
    private final StorageInventory inventory;

    public BasicStorage(ITextComponent title, int rows)
    {
        this.title = title;
        this.inventory = new StorageInventory(this, rows * 9);
    }

    @Override
    public StorageInventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public ITextComponent getStorageName()
    {
        return this.title;
    }
}
