package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityMoped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

import javax.vecmath.Vector3f;

/**
 * Author: MrCrayfish
 */
public class RenderMoped extends RenderLandVehicle<EntityMoped>
{
    private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    public final boolean isChristmas;

    public RenderMoped(RenderManager renderManager)
    {
        super(renderManager);
        this.setFuelPortPosition(-2.75F, 13.1F, -3.4F, 0.0F, -90.0F, 0.0F, 0.2F);
        this.addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0F, 1.7F, -6.7F, 1.5F);

        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void doRender(EntityMoped entity, double x, double y, double z, float currentYaw, float partialTicks)
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
            GlStateManager.scale(1.2, 1.2, 1.2);
            GlStateManager.translate(0, 0.1, 0.125);

            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * -20F, 0, 0, 1);

            this.setupBreakAnimation(entity, partialTicks);

            //Render body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.5625, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.5, 11.5 * 0.0625);
                GlStateManager.rotate(-22.5F, 1, 0, 0);

                float wheelScale = 1.3F;
                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;

                GlStateManager.rotate(turnRotation / 2, 0, 1, 0);
                GlStateManager.rotate(22.5F, 1, 0, 0);
                GlStateManager.translate(0, 0, -11.5 * 0.0625);

                //Render handles bars
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, 0.835, 0.525);
                    //GlStateManager.rotate(-22.5F, 1, 0, 0);
                    GlStateManager.scale(0.8, 0.8, 0.8);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.handleBar, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();

                //Render front bar and mud guard
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, -0.12, 0.785);
                    GlStateManager.rotate(-22.5F, 1, 0, 0);
                    GlStateManager.scale(0.9, 0.9, 0.9);
                    Minecraft.getMinecraft().getRenderItem().renderItem(entity.mudGuard, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();

                //Render front wheel
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, -0.4, 14.5 * 0.0625);
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

            super.doRender(entity, x, y, z, currentYaw, partialTicks);

            if(entity.hasChest())
            {
                //Render chest
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, 0.859, -0.675);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    GlStateManager.scale(1.0F, -1.0F, -1.0F);
                    GlStateManager.scale(0.6F, 0.6F, 0.6F);
                    GlStateManager.translate(-0.5F, -0.5F, -0.5F);

                    if(this.isChristmas)
                    {
                        this.bindTexture(TEXTURE_CHRISTMAS);
                    }
                    else
                    {
                        this.bindTexture(TEXTURE_NORMAL);
                    }
                    MOPED_CHEST.renderAll();
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
