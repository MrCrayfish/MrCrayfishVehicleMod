package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.RayTraceResultRotated;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
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

        if(entity.shouldRenderFuelPort())
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

    public void setEnginePosition(float x, float y, float z, float rotation, float scale)
    {
        this.enginePosition = new PartPosition(x, y, z, 0.0F, rotation, 0.0F, scale);
    }

    public void setFuelPortPosition(float x, float y, float z, float rotation)
    {
        this.setFuelPortPosition(x, y, z, 0.0F, rotation, 0.0F, 0.25F);
    }

    public void setFuelPortPosition(float x, float y, float z, float rotX, float rotY, float rotZ, float scale)
    {
        this.fuelPortBodyPosition = new PartPosition(x, y, z, rotX, rotY, rotZ, scale);
        this.fuelPortLidPosition = new PartPosition(x, y, z, rotX, rotY - 110.0F, rotZ, scale);
    }

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }
}
