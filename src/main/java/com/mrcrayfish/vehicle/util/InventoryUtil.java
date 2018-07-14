package com.mrcrayfish.vehicle.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class InventoryUtil
{
    public static void writeInventoryToNBT(NBTTagCompound compound, String tagName, @Nullable IInventory inventory)
    {
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) i);
                stack.writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        compound.setTag(tagName, tagList);
    }

    public static <T extends IInventory> T readInventoryToNBT(NBTTagCompound compound, String tagName, T t)
    {
        if(compound.hasKey(tagName, Constants.NBT.TAG_LIST))
        {
            NBTTagList tagList = compound.getTagList(tagName, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < tagList.tagCount(); i++)
            {
                NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
                byte slot = tagCompound.getByte("Slot");
                if(slot >= 0 && slot < t.getSizeInventory())
                {
                    t.setInventorySlotContents(slot, new ItemStack(tagCompound));
                }
            }
        }
        return t;
    }
}
