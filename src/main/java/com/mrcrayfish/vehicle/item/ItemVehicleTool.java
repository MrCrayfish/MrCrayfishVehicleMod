package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.Item;

public class ItemVehicleTool extends Item
{
    public ItemVehicleTool(String id)
    {
        this.setUnlocalizedName(id.replace(":", "."));
        this.setRegistryName(id);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
        this.setMaxStackSize(1);
    }
}