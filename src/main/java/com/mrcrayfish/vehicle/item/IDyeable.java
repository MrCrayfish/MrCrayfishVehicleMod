package com.mrcrayfish.vehicle.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public interface IDyeable
{
    boolean hasColor(ItemStack stack);

    int getColor(ItemStack stack);

    void setColor(ItemStack stack, int color);
}
