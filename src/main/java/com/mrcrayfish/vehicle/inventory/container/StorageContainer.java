package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.slot.SlotStorage;
import com.mrcrayfish.vehicle.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class StorageContainer extends Container
{
    private final IStorage storageInventory;
    private final int numRows;

    public StorageContainer(int windowId, IInventory playerInventory, IStorage storageInventory, PlayerEntity player)
    {
        super(ModContainers.STORAGE, windowId);
        this.storageInventory = storageInventory;
        this.numRows = storageInventory.getSizeInventory() / 9;
        storageInventory.openInventory(player);
        int yOffset = (this.numRows - 4) * 18;

        for(int i = 0; i < this.numRows; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new SlotStorage(storageInventory.getInventory(), j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + yOffset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 161 + yOffset));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.storageInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(index < this.numRows * 9)
            {
                if(!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if(itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.storageInventory.closeInventory(playerIn);
    }

    public IInventory getStorageInventory()
    {
        return this.storageInventory;
    }
}
