package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.ItemEngine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ContainerVehicle extends Container
{
    private final IInventory vehicleInventory;
    private final EntityPoweredVehicle vehicle;

    public ContainerVehicle(IInventory vehicleInventory, EntityPoweredVehicle vehicle, EntityPlayer player)
    {
        this.vehicleInventory = vehicleInventory;
        this.vehicle = vehicle;

        this.vehicleInventory.openInventory(player);

        this.addSlotToContainer(new Slot(ContainerVehicle.this.vehicleInventory, 0, 8, 17)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return vehicle.getEngineType() != EngineType.NONE && stack.getItem() instanceof ItemEngine && ((ItemEngine) stack.getItem()).getEngineType() == vehicle.getEngineType();
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        this.addSlotToContainer(new Slot(ContainerVehicle.this.vehicleInventory, 1, 8, 35)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return vehicle.canChangeWheels() && stack.getItem() == ModItems.WHEEL;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 160));
        }
    }

    public IInventory getVehicleInventory()
    {
        return vehicleInventory;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return vehicleInventory.isUsableByPlayer(playerIn) && vehicle.isEntityAlive() && vehicle.getDistance(playerIn) < 8.0F;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if(index < vehicleInventory.getSizeInventory())
            {
                if(!this.mergeItemStack(slotStack, vehicleInventory.getSizeInventory(), inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(this.getSlot(0).isItemValid(slotStack))
            {
                if(!this.mergeItemStack(slotStack, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(vehicleInventory.getSizeInventory() <= 1 || !this.mergeItemStack(slotStack, 1, vehicleInventory.getSizeInventory(), false))
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
        }

        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        vehicleInventory.closeInventory(playerIn);
    }
}
