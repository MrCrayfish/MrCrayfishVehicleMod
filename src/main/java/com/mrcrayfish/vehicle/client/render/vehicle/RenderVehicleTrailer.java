package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityVehicleTrailer;

/**
 * Author: MrCrayfish
 */
public class RenderVehicleTrailer extends AbstractRenderTrailer<EntityVehicleTrailer>
{
    @Override
    public void render(EntityVehicleTrailer entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.TRAILER_BODY.getModel());
        this.renderWheel(entity, false, -14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
        this.renderWheel(entity, true, 14.5F * 0.0625F, -0.5F, -2.5F * 0.0625F, 2.0F, partialTicks);
    }
}
