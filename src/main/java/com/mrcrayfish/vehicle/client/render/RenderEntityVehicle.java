package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: MrCrayfish
 */
public class RenderEntityVehicle<T extends EntityVehicle & EntityRaytracer.IEntityRaytraceable, R extends AbstractRenderVehicle<T>> extends Render<T>
{
    private final RenderVehicleWrapper<T, R> wrapper;

    public RenderEntityVehicle(RenderManager renderManager, RenderVehicleWrapper<T, R> wrapper)
    {
        super(renderManager);
        this.wrapper = wrapper;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(entity.isDead)
            return;

        if(entity.getRidingEntity() instanceof EntityJack)
            return;

        GlStateManager.pushMatrix();
        {
            //Enable the standard item lighting so vehicles render correctly
            RenderHelper.enableStandardItemLighting();

            //Translate and rotate using parameters
            GlStateManager.translate(x, y, z);
            wrapper.applyPreRotations(entity, partialTicks);
            GlStateManager.rotate(-entityYaw, 0, 1, 0);

            //Applies the break animation
            this.setupBreakAnimation(entity, partialTicks);

            //Render vehicle
            wrapper.render(entity, partialTicks);
        }
        GlStateManager.popMatrix();

        EntityRaytracer.renderRaytraceElements(entity, x, y, z, entityYaw);
    }

    private void setupBreakAnimation(EntityVehicle vehicle, float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        if(timeSinceHit > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit, 0, 0, 1);
        }
    }
}
