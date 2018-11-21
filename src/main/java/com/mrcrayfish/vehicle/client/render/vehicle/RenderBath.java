package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderPoweredVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityBath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Author: MrCrayfish
 */
public class RenderBath extends RenderPoweredVehicle<EntityBath>
{
    public RenderBath(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityBath entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity.getControllingPassenger();
        if(entityLivingBase != null)
        {
            entityLivingBase.renderYawOffset = currentYaw;
            entityLivingBase.prevRenderYawOffset = currentYaw;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.translate(0, -0.03125, -0.25);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyLevelToGround = 0.5;

            float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
            float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.rotate(90F, 0, 1, 0);
                GlStateManager.translate(0, bodyLevelToGround, 0);
                GlStateManager.rotate(bodyRoll, 1, 0, 0);
                GlStateManager.rotate(-bodyPitch, 0, 0, 1);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
