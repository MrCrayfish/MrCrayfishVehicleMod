package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.util.ItemNames;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class ItemKey extends Item implements IDyeable
{
    public ItemKey()
    {
        this.setUnlocalizedName(ItemNames.KEY.replace(":", "."));
        this.setRegistryName(ItemNames.KEY);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean hasColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return tagCompound.hasKey("color", Constants.NBT.TAG_INT);
    }

    @Override
    public int getColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return tagCompound.getInteger("color");
    }

    @Override
    public void setColor(ItemStack stack, int color)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        tagCompound.setInteger("color", color);
    }

    private static NBTTagCompound createTagCompound(ItemStack stack)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
