package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityOffRoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Author: MrCrayfish
 */
public class RenderOffRoader extends RenderLandVehicle<EntityOffRoader>
{
    public RenderOffRoader(RenderManager renderManager)
    {
        super(renderManager);
        this.setFuelPortPosition(-12.25F, 20.5F, -7.3F, -90.0F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 5.5F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 5.5F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, 5.5F, -14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, 5.5F, -14.5F, 2.25F);
    }

    @Override
    public void doRender(EntityOffRoader entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity.getControllingPassenger();
        if(entityLivingBase != null)
        {
            entityLivingBase.renderYawOffset = currentYaw - additionalYaw;
            entityLivingBase.prevRenderYawOffset = currentYaw - additionalYaw;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.rotate(additionalYaw, 0, 1, 0);
            GlStateManager.translate(0, 0, -0.125);
            GlStateManager.scale(1.4, 1.4, 1.4);

            this.setupBreakAnimation(entity, partialTicks);
            super.doRender(entity, x, y, z, currentYaw, partialTicks);

            double bodyOffset = 0.775;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            //Render the handles bars
            GlStateManager.pushMatrix();
            {
                // Positions the steering wheel in the correct position
                GlStateManager.translate(-0.3125, bodyOffset + 0.35, 0.2);
                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.translate(0, -0.02, 0);
                GlStateManager.scale(0.75, 0.75, 0.75);

                // Rotates the steering wheel based on the wheel angle
                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
