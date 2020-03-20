package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class EditVehicleContainer extends Container
{
    private final IInventory vehicleInventory;
    private final PoweredVehicleEntity vehicle;

    public EditVehicleContainer(int windowId, IInventory vehicleInventory, PoweredVehicleEntity vehicle, PlayerEntity player, PlayerInventory playerInventory)
    {
        super(ModContainers.EDIT_VEHICLE.get(), windowId);
        this.vehicleInventory = vehicleInventory;
        this.vehicle = vehicle;

        this.vehicleInventory.openInventory(player);

        this.addSlot(new Slot(EditVehicleContainer.this.vehicleInventory, 0, 8, 17)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return vehicle.getEngineType() != EngineType.NONE && stack.getItem() instanceof EngineItem && ((EngineItem) stack.getItem()).getEngineType() == vehicle.getEngineType();
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        this.addSlot(new Slot(EditVehicleContainer.this.vehicleInventory, 1, 8, 35)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return vehicle.canChangeWheels() && stack.getItem() instanceof WheelItem;
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
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160));
        }
    }

    public IInventory getVehicleInventory()
    {
        return vehicleInventory;
    }

    public PoweredVehicleEntity getVehicle()
    {
        return vehicle;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return vehicleInventory.isUsableByPlayer(player) && vehicle.isAlive() && vehicle.getDistance(player) < 8.0F;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
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
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);
        vehicleInventory.closeInventory(player);
    }
}
