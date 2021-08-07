package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.entity.IWheelType;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class WheelItem extends PartItem implements IDyeable
{
    private IWheelType wheelType;

    public WheelItem(IWheelType wheelType, Item.Properties properties)
    {
        super(properties);
        this.wheelType = wheelType;
    }

    public IWheelType getWheelType()
    {
        return this.wheelType;
    }
}
