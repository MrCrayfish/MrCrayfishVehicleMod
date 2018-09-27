package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RenderLandVehicle<T extends EntityLandVehicle> extends RenderPoweredVehicle<T>
{
    private List<Wheel> wheels = new ArrayList<>();

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

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ)
    {
        wheels.add(new Wheel(side, position, 2.0F, 1.0F, offsetX, 0F, offsetZ));
    }

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, 0F, offsetZ));
    }

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, offsetY, offsetZ));
    }
}
