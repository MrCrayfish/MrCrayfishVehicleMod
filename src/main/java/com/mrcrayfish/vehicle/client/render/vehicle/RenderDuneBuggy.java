package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.RenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityDuneBuggy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderDuneBuggy extends RenderVehicle<EntityDuneBuggy>
{
    public RenderDuneBuggy(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDuneBuggy entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityDuneBuggy entity, double x, double y, double z, float currentYaw, float partialTicks)
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
            GlStateManager.scale(1.3, 1.3, 1.3);
            GlStateManager.translate(0, 0.0, 0.165);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyLevelToGround = 0.375;
            double bodyOffset = 0.13;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyLevelToGround + bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
            double wheelScale = 1.0F;

            //Render the handles bars
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.5, 3.125 * 0.0625);
                GlStateManager.rotate(-22.5F, 1, 0, 0);
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 15F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);
                GlStateManager.rotate(22.5F, 1, 0, 0);
                GlStateManager.translate(0, 0, -0.2);

                //TODO change to entity itemstack instance
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.handleBar, ItemCameraTransforms.TransformType.NONE);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, -bodyOffset - 0.225, 0.33);
                    float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            float rearWheelSpin = entity.prevRearWheelRotation + (entity.rearWheelRotation - entity.prevRearWheelRotation) * partialTicks;

            double offsetCenter = 0.35625;
            double wheelZOffset = 0.145;
            double wheelYOffset = bodyOffset + 0.01875;

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0.3, wheelYOffset, -offsetCenter);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
                    }

                    GlStateManager.translate(0.0625 * wheelScale - wheelZOffset, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-0.6, 0, 0);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(-0.0625 * wheelScale + wheelZOffset, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
