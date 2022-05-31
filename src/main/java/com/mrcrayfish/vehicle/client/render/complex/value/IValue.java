package com.mrcrayfish.vehicle.client.render.complex.value;

import com.mrcrayfish.vehicle.entity.VehicleEntity;

/**
 * Author: MrCrayfish
 */
public interface IValue
{
    double getValue(VehicleEntity entity, float partialTicks);
}
