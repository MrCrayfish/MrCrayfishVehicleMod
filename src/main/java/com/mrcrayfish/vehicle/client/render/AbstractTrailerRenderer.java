package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.entity.EntityType;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractTrailerRenderer<T extends TrailerEntity> extends AbstractVehicleRenderer<T>
{
    public AbstractTrailerRenderer(EntityType<T> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }
}
