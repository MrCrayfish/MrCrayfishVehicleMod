package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.item.ItemWheel;
import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Author: MrCrayfish
 */
public class ContainerWorkstation extends Container
{
    private TileEntityWorkstation tileEntityWorkstation;
    private BlockPos pos;

    public ContainerWorkstation(IInventory playerInventory, TileEntityWorkstation tileEntityWorkstation)
    {
        this.tileEntityWorkstation = tileEntityWorkstation;
        this.pos = tileEntityWorkstation.getPos();

        this.addSlotToContainer(new Slot(tileEntityWorkstation, 0, 187, 30)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == Items.DYE;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        this.addSlotToContainer(new Slot(tileEntityWorkstation, 1, 207, 30)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof ItemEngine;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        this.addSlotToContainer(new Slot(tileEntityWorkstation, 2, 227, 30)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof ItemWheel;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                this.addSlotToContainer(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 120 + x * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 178));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if(index < 3)
            {
                if(!this.mergeItemStack(slotStack, 3, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if(slotStack.getItem() instanceof ItemDye)
                {
                    if(!this.mergeItemStack(slotStack, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(slotStack.getItem() instanceof ItemEngine)
                {
                    if(!this.mergeItemStack(slotStack, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(slotStack.getItem() instanceof ItemWheel)
                {
                    if(!this.mergeItemStack(slotStack, 2, 3, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 31)
                {
                    if(!this.mergeItemStack(slotStack, 31, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 39 && !this.mergeItemStack(slotStack, 3, 31, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if(slotStack.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return stack;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public TileEntityWorkstation getTileEntity()
    {
        return tileEntityWorkstation;
    }
}
