package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;

import net.minecraft.item.Item;

public class ItemWrench extends Item
{
    public ItemWrench()
    {
        setUnlocalizedName("wrench");
        setRegistryName("wrench");
        setCreativeTab(VehicleMod.CREATIVE_TAB);
    }
}