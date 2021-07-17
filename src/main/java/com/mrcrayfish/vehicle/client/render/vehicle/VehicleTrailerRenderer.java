package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.VehicleEntityTrailer;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class VehicleTrailerRenderer extends AbstractTrailerRenderer<VehicleEntityTrailer>
{
    public VehicleTrailerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable VehicleEntityTrailer vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.VEHICLE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 1.25F, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 1.25F, partialTicks, light);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.VEHICLE_TRAILER, parts, transforms);
        };
    }
}
