package com.mrcrayfish.vehicle.client.render.complex.transforms;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;

/**
 * Author: MrCrayfish
 */
public interface Transform
{
    void apply(VehicleEntity entity, MatrixStack stack, float partialTicks);

    MatrixTransform create(VehicleEntity entity, float partialTicks);
}
