package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
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

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to apply to the part
     * @param part the part to render onto the vehicle
     */
    protected void renderPart(@Nullable PartPosition position, ItemStack part)
    {
        if(position == null)
            return;

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(position.getScale(), position.getScale(), position.getScale());
            GlStateManager.translate(0, 0.5, 0);
            GlStateManager.rotate((float) position.getRotX(), 1, 0, 0);
            GlStateManager.rotate((float) position.getRotY(), 0, 1, 0);
            GlStateManager.rotate((float) position.getRotZ(), 0, 0, 1);
            Minecraft.getMinecraft().getRenderItem().renderItem(part, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }
}
