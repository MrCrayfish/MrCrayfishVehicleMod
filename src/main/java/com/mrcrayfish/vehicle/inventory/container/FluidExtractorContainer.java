package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.inventory.container.slot.FuelSlot;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorContainer extends Container
{
    private int extractionProgress;
    private int remainingFuel;
    private int maxFuelProgress;
    private int fluidLevel;

    private FluidExtractorTileEntity fluidExtractor;

    public FluidExtractorContainer(int windowId, IInventory playerInventory, FluidExtractorTileEntity fluidExtractor)
    {
        super(ModContainers.FLUID_EXTRACTOR.get(), windowId);
        this.fluidExtractor = fluidExtractor;

        this.addSlot(new FuelSlot(fluidExtractor, 0, 33, 34));
        this.addSlot(new Slot(fluidExtractor, 1, 64, 33));

        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                this.addSlot(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }

        this.addDataSlots(fluidExtractor.getFluidExtractorData());
    }

    public FluidExtractorTileEntity getFluidExtractor()
    {
        return fluidExtractor;
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

            if(index == 0 || index == 1)
            {
                if(!this.moveItemStackTo(slotStack, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if(this.fluidExtractor.canPlaceItem(1, slotStack))
                {
                    if(!this.moveItemStackTo(slotStack, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(ForgeHooks.getBurnTime(slotStack) > 0)
                {
                    if(!this.moveItemStackTo(slotStack, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 29)
                {
                    if(!this.moveItemStackTo(slotStack, 29, 38, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 38 && !this.moveItemStackTo(slotStack, 2, 29, false))
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
}
