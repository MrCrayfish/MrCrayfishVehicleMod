package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.util.ItemNames;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemHammer extends Item
{
    public ItemHammer()
    {
        this.setUnlocalizedName(ItemNames.HAMMER.replace(":", "."));
        this.setRegistryName(ItemNames.HAMMER);
        this.setMaxDamage(200);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }
}
