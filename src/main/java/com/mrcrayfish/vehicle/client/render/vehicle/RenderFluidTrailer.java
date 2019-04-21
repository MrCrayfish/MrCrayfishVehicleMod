package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.Models;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityFluidTrailer;

/**
 * Author: MrCrayfish
 */
public class RenderFluidTrailer extends AbstractRenderTrailer<EntityFluidTrailer>
{
    @Override
    public void render(EntityFluidTrailer entity, float partialTicks)
    {
        this.renderDamagedPart(entity, entity.body, Models.FLUID_TRAILER.getModel());
        this.renderWheel(entity, false, -11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 11.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
    }
}
