package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.VehicleEntityTrailer;
import net.minecraft.client.renderer.IRenderTypeBuffer;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleTrailer extends AbstractRenderTrailer<VehicleEntityTrailer>
{
    @Override
    public void render(VehicleEntityTrailer entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.VEHICLE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, -14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, 14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks, light);
    }
}
