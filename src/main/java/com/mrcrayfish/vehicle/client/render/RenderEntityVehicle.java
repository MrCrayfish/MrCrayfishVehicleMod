package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class RenderEntityVehicle<T extends VehicleEntity & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends EntityRenderer<T>
{
    private final RenderVehicleWrapper<T, R> wrapper;

    public RenderEntityVehicle(EntityRendererManager renderManager, RenderVehicleWrapper<T, R> wrapper)
    {
        super(renderManager);
        this.wrapper = wrapper;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(!entity.isAlive())
            return;

        if(entity.getRidingEntity() instanceof EntityJack)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        wrapper.applyPreRotations(entity, partialTicks);
        GlStateManager.rotatef(-entityYaw, 0, 1, 0);
        this.setupBreakAnimation(entity, partialTicks);
        wrapper.render(entity, partialTicks);
        GlStateManager.popMatrix();

        EntityRaytracer.renderRaytraceElements(entity, x, y, z, entityYaw);
    }

    private void setupBreakAnimation(VehicleEntity vehicle,float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        if(timeSinceHit > 0.0F)
        {
            GlStateManager.rotatef(MathHelper.sin(timeSinceHit) * timeSinceHit, 0, 0, 1);
        }
    }
}
