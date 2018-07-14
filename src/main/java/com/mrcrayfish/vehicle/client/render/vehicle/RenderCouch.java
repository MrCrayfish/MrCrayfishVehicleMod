package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityCouch;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends RenderVehicle<EntityCouch>
{
    public RenderCouch(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityCouch entity, double x, double y, double z, float currentYaw, float partialTicks)
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
            GlStateManager.translate(0, -0.03125, 0.1);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyLevelToGround = 0.4375;
            double bodyOffset = 4.375 * 0.0625;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.rotate(90F, 0, 1, 0);
                GlStateManager.translate(0, bodyLevelToGround + bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;

            //Render the handles bars
           /* GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.7 + bodyOffset, 0.25);
                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.translate(0, 0.02, 0);

                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 15F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                //TODO change to entity itemstack instance
                Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(ModItems.ATV_HANDLE_BAR), ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();*/

            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            float rearWheelSpin = entity.prevRearWheelRotation + (entity.rearWheelRotation - entity.prevRearWheelRotation) * partialTicks;

            double wheelScale = 1.95F;
            double offsetCenter = 0.45;
            double wheelYOffset = bodyOffset + 0.03125;
            double wheelZOffset = 0.58;

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(wheelZOffset, wheelYOffset, offsetCenter);
                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-(wheelZOffset * 2), 0, 0);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(-0.0625 * wheelScale + 0.0625, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(wheelZOffset, wheelYOffset, -offsetCenter);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
                    }

                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-(wheelZOffset * 2), 0, 0);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(-0.0625 * wheelScale + 0.0625, 0.0, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
