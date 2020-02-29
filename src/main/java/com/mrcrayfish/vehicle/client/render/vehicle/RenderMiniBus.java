package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;

/**
 * Author: MrCrayfish
 */
public class RenderMiniBus extends AbstractRenderVehicle<MiniBusEntity>
{
    @Override
    public ISpecialModel getBodyModel()
    {
        return SpecialModels.MINI_BUS_BODY;
    }

    @Override
    public void render(MiniBusEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.MINI_BUS_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }
}
