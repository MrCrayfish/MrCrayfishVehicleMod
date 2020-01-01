package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class HammerItem extends Item
{
    public HammerItem()
    {
        super(new Item.Properties().maxDamage(200).group(VehicleMod.CREATIVE_TAB));
        this.setRegistryName(Names.Item.HAMMER);
    }
}
