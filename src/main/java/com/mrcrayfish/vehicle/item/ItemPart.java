package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.Item;

import static com.mrcrayfish.vehicle.Reference.MOD_ID;

/**
 * Author: MrCrayfish
 */
public class ItemPart extends Item
{
    public ItemPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(MOD_ID, id);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }
}
