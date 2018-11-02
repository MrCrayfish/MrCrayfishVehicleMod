package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.tileentity.TileEntityFluidMixer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class ContainerFluidMixer extends Container
{
    private int extractionProgress;
    private int remainingFuel;
    private int maxFuelProgress;
    private int blazeLevel;
    private int enderSapLevel;
    private int fueliumLevel;

    private TileEntityFluidMixer fluidExtractor;

    public ContainerFluidMixer(IInventory playerInventory, TileEntityFluidMixer fluidExtractor)
    {
        this.fluidExtractor = fluidExtractor;

        this.addSlotToContainer(new SlotFurnaceFuel(fluidExtractor, 0, 9, 50));
        this.addSlotToContainer(new Slot(fluidExtractor, 1, 103, 41));

        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                this.addSlotToContainer(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 98 + x * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 156));
        }
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.fluidExtractor);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for(IContainerListener listener : this.listeners)
        {
            if(this.extractionProgress != this.fluidExtractor.getField(0))
            {
                listener.sendWindowProperty(this, 0, this.fluidExtractor.getField(0));
            }
            if(this.remainingFuel != this.fluidExtractor.getField(1))
            {
                listener.sendWindowProperty(this, 1, this.fluidExtractor.getField(1));
            }
            if(this.maxFuelProgress != this.fluidExtractor.getField(2))
            {
                listener.sendWindowProperty(this, 2, this.fluidExtractor.getField(2));
            }
            if(this.blazeLevel != this.fluidExtractor.getField(3))
            {
                listener.sendWindowProperty(this, 3, this.fluidExtractor.getField(3));
            }
            if(this.enderSapLevel != this.fluidExtractor.getField(4))
            {
                listener.sendWindowProperty(this, 4, this.fluidExtractor.getField(4));
            }
            if(this.fueliumLevel != this.fluidExtractor.getField(5))
            {
                listener.sendWindowProperty(this, 5, this.fluidExtractor.getField(5));
            }
        }

        this.extractionProgress = this.fluidExtractor.getField(0);
        this.remainingFuel = this.fluidExtractor.getField(1);
        this.maxFuelProgress = this.fluidExtractor.getField(2);
        this.blazeLevel = this.fluidExtractor.getField(3);
        this.enderSapLevel = this.fluidExtractor.getField(4);
        this.fueliumLevel = this.fluidExtractor.getField(5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        this.fluidExtractor.setField(id, value);
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

            if(index == 0 || index == 1)
            {
                if(!this.mergeItemStack(slotStack, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index > 1)
            {
                if(fluidExtractor.isItemValidForSlot(1, slotStack))
                {
                    if(!this.mergeItemStack(slotStack, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(TileEntityFurnace.isItemFuel(slotStack))
                {
                    if(!this.mergeItemStack(slotStack, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index >= 2 && index < 29)
                {
                    if(!this.mergeItemStack(slotStack, 29, 38, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index >= 29 && index < 38 && !this.mergeItemStack(slotStack, 2, 29, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(slotStack, 2, 38, false))
            {
                return ItemStack.EMPTY;
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
}
