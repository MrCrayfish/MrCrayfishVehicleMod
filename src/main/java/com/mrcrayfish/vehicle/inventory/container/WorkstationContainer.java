package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Author: MrCrayfish
 */
public class WorkstationContainer extends Container
{
    private WorkstationTileEntity workstationTileEntity;
    private BlockPos pos;

    public WorkstationContainer(int windowId, IInventory playerInventory, WorkstationTileEntity workstationTileEntity)
    {
        super(ModContainers.WORKSTATION.get(), windowId);
        this.workstationTileEntity = workstationTileEntity;
        this.pos = workstationTileEntity.getBlockPos();

        this.addSlot(new Slot(workstationTileEntity, 0, 173, 30)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() instanceof DyeItem;
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });

        this.addSlot(new Slot(workstationTileEntity, 1, 193, 30)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() instanceof EngineItem;
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });

        this.addSlot(new Slot(workstationTileEntity, 2, 213, 30)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() instanceof WheelItem;
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });

        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 102 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 160));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if(index < 3)
            {
                if(!this.moveItemStackTo(slotStack, 3, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if(slotStack.getItem() instanceof DyeItem)
                {
                    if(!this.moveItemStackTo(slotStack, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(slotStack.getItem() instanceof EngineItem)
                {
                    if(!this.moveItemStackTo(slotStack, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(slotStack.getItem() instanceof WheelItem)
                {
                    if(!this.moveItemStackTo(slotStack, 2, 3, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 31)
                {
                    if(!this.moveItemStackTo(slotStack, 31, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 39 && !this.moveItemStackTo(slotStack, 3, 31, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
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

    public WorkstationTileEntity getTileEntity()
    {
        return workstationTileEntity;
    }
}
