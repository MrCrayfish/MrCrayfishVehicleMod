package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class KeyItem extends Item implements IDyeable
{
    public KeyItem()
    {
        super(new Item.Properties().maxStackSize(1).group(VehicleMod.CREATIVE_TAB));
        this.setRegistryName(Names.Item.KEY);
    }
}
