package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class RenderPoweredVehicle<T extends EntityPoweredVehicle & EntityRaytracer.IEntityRaytraceable> extends RenderVehicle<T, AbstractRenderVehicle<T>>
{
    private PartPosition enginePosition;
    private PartPosition fuelPortBodyPosition;
    private PartPosition fuelPortLidPosition;

    protected RenderPoweredVehicle(RenderManager renderManager)
    {
        super(renderManager, null);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(entity.shouldRenderEngine())
        {
            this.renderPart(enginePosition, entity.engine);
        }

        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            RayTraceResultRotated result = EntityRaytracer.getContinuousInteraction();
            if (result != null && result.entityHit == entity && result.equalsContinuousInteraction(EntityRaytracer.FUNCTION_FUELING))
            {
                this.renderPart(fuelPortBodyPosition, entity.fuelPortBody);
                if(this.shouldRenderFuelLid())
                {
                    this.renderPart(fuelPortLidPosition, entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(fuelPortBodyPosition, entity.fuelPortClosed);
                entity.playFuelPortCloseSound();
            }
        }
    }

    public void setEnginePosition(double x, double y, double z, double rotation, double scale)
    {
        this.enginePosition = new PartPosition(x, y, z, 0, rotation, 0, scale);
    }

    public void setFuelPortPosition(double x, double y, double z, double rotation)
    {
        this.setFuelPortPosition(x, y, z, 0, rotation, 0, 0.25);
    }

    public void setFuelPortPosition(double x, double y, double z, double rotX, double rotY, double rotZ, double scale)
    {
        this.fuelPortBodyPosition = new PartPosition(x, y, z, rotX, rotY, rotZ, scale);
        this.fuelPortLidPosition = new PartPosition(x, y, z, rotX, rotY - 110, rotZ, scale);
    }

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }
}
