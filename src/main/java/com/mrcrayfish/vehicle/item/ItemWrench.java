package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.Item;

public class ItemWrench extends Item
{
    public ItemWrench()
    {
        this.setUnlocalizedName("wrench");
        this.setRegistryName("wrench");
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
        this.setMaxStackSize(1);
    }
}