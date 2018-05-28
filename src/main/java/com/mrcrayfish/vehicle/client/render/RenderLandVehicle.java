package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicle<T extends EntityLandVehicle> extends RenderVehicle<T>
{
    protected List<Wheel> wheels = new ArrayList<>();

    protected RenderLandVehicle(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        wheels.forEach(wheel -> wheel.render(entity, partialTicks));
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
