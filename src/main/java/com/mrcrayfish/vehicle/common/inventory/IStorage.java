package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IStorage extends IInventory
{
    StorageInventory getInventory();

    /*@Override
    default int[] getSlotsForFace(Direction side)
    {
        int[] slots = new int[this.getInventory().getSizeInventory()];
        for(int i = 0; i < this.getInventory().getSizeInventory(); i++)
        {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    default boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    default boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        return true;
    }
*/
    @Override
    default int getSizeInventory()
    {
        return this.getInventory().getSizeInventory();
    }

    @Override
    default boolean isEmpty()
    {
        return this.getInventory().isEmpty();
    }

    @Override
    default ItemStack getStackInSlot(int index)
    {
        return this.getInventory().getStackInSlot(index);
    }

    @Override
    default ItemStack decrStackSize(int index, int count)
    {
        return this.getInventory().decrStackSize(index, count);
    }

    @Override
    default ItemStack removeStackFromSlot(int index)
    {
        return this.getInventory().removeStackFromSlot(index);
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack stack)
    {
        this.getInventory().setInventorySlotContents(index, stack);
    }

    @Override
    default int getInventoryStackLimit()
    {
        return this.getInventory().getInventoryStackLimit();
    }

    @Override
    default void markDirty()
    {
        this.getInventory().markDirty();
    }

    @Override
    default boolean isUsableByPlayer(PlayerEntity player)
    {
        return this.getInventory().isUsableByPlayer(player);
    }

    @Override
    default void openInventory(PlayerEntity player)
    {
        this.getInventory().openInventory(player);
    }

    @Override
    default void closeInventory(PlayerEntity player)
    {
        this.getInventory().openInventory(player);
    }

    @Override
    default boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    default void clear()
    {
        getInventory().clear();
    }

    default boolean isStorageItem(ItemStack stack)
    {
        return true;
    }

    ITextComponent getStorageName();

    default INamedContainerProvider getStorageContainerProvider()
    {
        return new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> new StorageContainer(windowId, playerInventory, this, playerEntity), this.getStorageName());
    }
}
