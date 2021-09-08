package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractTrailerRenderer<T extends TrailerEntity> extends AbstractVehicleRenderer<T>
{
    public AbstractTrailerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }
}
