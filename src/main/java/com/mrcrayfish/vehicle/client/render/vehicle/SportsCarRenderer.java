package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsCarEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SportsCarRenderer extends AbstractLandVehicleRenderer<SportsCarEntity>
{
    public SportsCarRenderer(VehicleProperties properties)
    {
        super(properties);
    }

    @Override
    protected void render(@Nullable SportsCarEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_CAR_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) -> {
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_CAR_BODY, parts, transforms);
        };
    }
}
