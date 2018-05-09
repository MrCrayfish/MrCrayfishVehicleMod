package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemPart extends Item
{
    public ItemPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }
}
