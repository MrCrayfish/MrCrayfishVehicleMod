package com.mrcrayfish.vehicle.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class InventoryUtil
{
    private static final Random RANDOM = new Random();

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

    public static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory)
    {
        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        float offsetX = RANDOM.nextFloat() * 0.25F + 0.1F;
        float offsetY = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetZ = RANDOM.nextFloat() * 0.25F + 0.1F;

        while (!stack.isEmpty())
        {
            EntityItem entity = new EntityItem(worldIn, x + (double)offsetX, y + (double)offsetY, z + (double)offsetZ, stack.splitStack(RANDOM.nextInt(21) + 10));
            entity.motionX = RANDOM.nextGaussian() * 0.05000000074505806D;
            entity.motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            entity.motionZ = RANDOM.nextGaussian() * 0.05000000074505806D;
            worldIn.spawnEntity(entity);
        }
    }
}
