package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class RenderVehicle<T extends EntityVehicle> extends Render<T>
{
    protected RenderVehicle(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    public void setupBreakAnimation(EntityVehicle vehicle, float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        float damageTaken = vehicle.getDamageTaken() - partialTicks;

        if (damageTaken < 0.0F)
        {
            damageTaken = 0.0F;
        }

        if (timeSinceHit > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10.0F, 0, 0, 1);
        }
    }
}
