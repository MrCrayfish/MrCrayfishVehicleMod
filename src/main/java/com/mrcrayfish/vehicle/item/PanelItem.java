package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class PanelItem extends Item
{
    public PanelItem()
    {
        super(new Item.Properties().group(VehicleMod.CREATIVE_TAB));
        this.setRegistryName(Names.Item.PANEL);
    }
}
