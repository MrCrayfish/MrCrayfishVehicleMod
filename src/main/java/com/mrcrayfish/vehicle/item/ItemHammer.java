package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemHammer extends Item
{
    public ItemHammer()
    {
        this.setUnlocalizedName("hammer");
        this.setRegistryName("hammer");
        this.setMaxDamage(200);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }
}
