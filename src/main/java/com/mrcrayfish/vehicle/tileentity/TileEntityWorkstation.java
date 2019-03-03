package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.inventory.IStorageBlock;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

/**
 * Author: MrCrayfish
 */
public class TileEntityWorkstation extends TileEntitySynced implements IStorageBlock
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public String getName()
    {
        return "vehicle.container.workstation";
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        ItemStackHelper.saveAllItems(compound, this.inventory);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, this.inventory);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index != 0 || (stack.getItem() instanceof ItemDye && inventory.get(index).getCount() < 1);
    }
}
