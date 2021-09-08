package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.VehicleTrailerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class VehicleTrailerRenderer extends AbstractTrailerRenderer<VehicleTrailerEntity>
{
    public VehicleTrailerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable VehicleTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.VEHICLE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.VEHICLE_TRAILER, parts, transforms);
        };
    }
}
