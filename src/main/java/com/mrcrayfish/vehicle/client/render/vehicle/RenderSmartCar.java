package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityGoKart;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySmartCar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderSmartCar extends RenderLandVehicle<EntitySmartCar>
{
    public RenderSmartCar(RenderManager renderManager)
    {
        super(renderManager);
        this.setEnginePosition(0F, 7.5F, -9F, 180F, 1.2F);
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7F, 12F, 1.5F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7F, 12F, 1.5F));
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7F, -12F, 1.5F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7F, -12F, 1.5F));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySmartCar entity)
    {
        return null;
    }

    @Override
    public void doRender(EntitySmartCar entity, double x, double y, double z, float currentYaw, float partialTicks)
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
            GlStateManager.translate(0, 0, 0.2);
            GlStateManager.scale(1.25, 1.25, 1.25);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyOffset = 0.6;

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
                GlStateManager.translate(0, bodyOffset + 0.2, 0.3);
                GlStateManager.rotate(-67.5F, 1, 0, 0);
                GlStateManager.translate(0, -0.02, 0);
                GlStateManager.scale(0.9, 0.9, 0.9);

                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            GlStateManager.translate(0, 3.5F * 0.0625F, 0);
            super.doRender(entity, x, y, z, currentYaw, partialTicks);
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
