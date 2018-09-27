package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityGolfCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Author: MrCrayfish
 */
public class RenderGolfCart extends RenderLandVehicle<EntityGolfCart>
{
    public RenderGolfCart(RenderManager renderManager)
    {
        super(renderManager);
        this.setFuelPortPosition(-13.25F, 15.5F, -7.3F, -90.0F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 4.5F, 16.0F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 4.5F, 16.0F, 1.75F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, 4.5F, -12.5F, 1.75F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, 4.5F, -12.5F, 1.75F);
    }

    @Override
    public void doRender(EntityGolfCart entity, double x, double y, double z, float currentYaw, float partialTicks)
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
            GlStateManager.scale(1.15, 1.15, 1.15);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyOffset = 0.5 + 0.25;

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
                GlStateManager.translate(-0.345, bodyOffset + 0.425, 0.1);
                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.translate(0, -0.02, 0);
                GlStateManager.scale(0.95, 0.95, 0.95);

                // Rotates the steering wheel based on the wheel angle
                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            super.doRender(entity, x, y, z, currentYaw, partialTicks);
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
