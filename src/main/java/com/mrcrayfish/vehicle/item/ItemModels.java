package com.mrcrayfish.vehicle.item;

import net.minecraft.item.Item;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ItemModels extends Item implements SubItems
{
    public ItemModels()
    {
        this.setUnlocalizedName("models");
        this.setRegistryName("models");
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        return NonNullList.create();
    }
}
