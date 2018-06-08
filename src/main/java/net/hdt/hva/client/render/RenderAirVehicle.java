package net.hdt.hva.client.render;

import com.mrcrayfish.vehicle.client.render.RenderVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import net.hdt.hva.entity.EntityAirVehicle;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RenderAirVehicle<T extends EntityAirVehicle> extends RenderVehicle<T>
{
    protected List<Wheel> wheels = new ArrayList<>();

    protected RenderAirVehicle(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
//        wheels.forEach(wheel -> wheel.render(entity, partialTicks));
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
