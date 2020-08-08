package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends Inventory implements INamedContainerProvider
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

    @Override
    public ITextComponent getDisplayName()
    {
        return this.wrapper.getStorageName();
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return this.wrapper.getStorageContainerProvider().createMenu(windowId, playerInventory, playerEntity);
    }

    public ListNBT write()
    {
        ListNBT tagList = new ListNBT();
        for(int i = 0; i < this.getSizeInventory(); i++)
        {
            ItemStack stack = this.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                CompoundNBT slotTag = new CompoundNBT();
                slotTag.putByte("Slot", (byte) i);
                stack.write(slotTag);
                tagList.add(slotTag);
            }
        }
        return tagList;
    }

    @Override
    public void read(ListNBT tagList)
    {
        this.clear();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT slotTag = tagList.getCompound(i);
            byte slot = slotTag.getByte("Slot");
            if(slot >= 0 && slot < this.getSizeInventory())
            {
                this.setInventorySlotContents(slot, ItemStack.read(slotTag));
            }
        }
    }
}
