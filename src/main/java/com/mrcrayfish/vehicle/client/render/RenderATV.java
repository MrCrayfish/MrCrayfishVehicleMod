package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityATV;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends Render<EntityATV>
{
    public static final EntityItem BODY = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(ModItems.BODY));
    public static final EntityItem WHEEL = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(ModItems.WHEEL));

    public static float offsetRotationYaw = 0F;
    public static float currentOffsetRotationYaw = 0F;

    static
    {
        BODY.hoverStart = 0F;
        WHEEL.hoverStart = 0F;
    }

    public RenderATV(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityATV entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityATV entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
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
            GlStateManager.scale(1.2, 1.2, 1.2);
            GlStateManager.translate(0, 0.125, 0.2);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.125, 0);
                Minecraft.getMinecraft().getRenderManager().renderEntity(BODY, 0, 0, 0, 0F, 0F, true);
            }
            GlStateManager.popMatrix();

            float wheelSpin = entity.prevWheelRotation + (entity.wheelRotation - entity.prevWheelRotation) * partialTicks;

            RenderHelper.disableStandardItemLighting();

            double wheelScale = 2F;
            double offsetCenter = 0.625;

            GlStateManager.pushMatrix();
            {
                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                GlStateManager.translate(0.3, 0.13125, offsetCenter);
                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-wheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-0.6, 0, 0);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-wheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(-0.0625 * wheelScale + 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0.3, 0.13125, -offsetCenter);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-wheelSpin, 1, 0, 0);
                    }

                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-0.6, 0, 0);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-wheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(-0.0625 * wheelScale + 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();
    }
}
