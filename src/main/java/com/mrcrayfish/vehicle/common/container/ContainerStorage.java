package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.common.slot.SlotStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ContainerStorage extends Container
{
    private final IInventory storageInventory;
    private final int numRows;

    public ContainerStorage(IInventory playerInventory, StorageInventory storageInventory, EntityPlayer player)
    {
        this.storageInventory = storageInventory;
        this.numRows = storageInventory.getSizeInventory() / 9;
        storageInventory.openInventory(player);
        int yOffset = (this.numRows - 4) * 18;

        for(int i = 0; i < this.numRows; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new SlotStorage(storageInventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + yOffset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 161 + yOffset));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.storageInventory.isUsableByPlayer(playerIn);
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
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

    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.storageInventory.closeInventory(playerIn);
    }

    public IInventory getStorageInventory()
    {
        return this.storageInventory;
    }
}
