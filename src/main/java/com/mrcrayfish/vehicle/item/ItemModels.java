package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.util.ItemNames;
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
        this.setUnlocalizedName(ItemNames.MODELS.replace(":", "."));
        this.setRegistryName(ItemNames.MODELS);
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        return NonNullList.create();
    }
}
