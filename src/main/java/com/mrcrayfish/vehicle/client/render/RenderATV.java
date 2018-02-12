package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityATV;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends Render<EntityATV>
{
    private static final EntityItem BODY = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(ModItems.BODY));
    private static final EntityItem WHEEL = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(ModItems.WHEEL));
    private static final EntityItem HANDLE_BAR = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(ModItems.HANDLE_BAR));

    static
    {
        BODY.hoverStart = 0F;
        WHEEL.hoverStart = 0F;
        HANDLE_BAR.hoverStart = 0F;
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
        BODY.setItem(entity.body);

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
            GlStateManager.scale(1.25, 1.25, 1.25);
            GlStateManager.translate(0, 0.15, 0.2);

            this.setupBreakAnimation(entity, partialTicks);

            RenderHelper.disableStandardItemLighting();

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.1875, 0);
                Minecraft.getMinecraft().getRenderManager().renderEntity(BODY, 0, 0, 0, 0F, 0F, true);
            }
            GlStateManager.popMatrix();

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;

            //Render the handles bars
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.55, 0.5);
                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.translate(0, 0.02, 0);

                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 15F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderManager().renderEntity(HANDLE_BAR, 0, 0, 0, 0F, 0F, true);
            }
            GlStateManager.popMatrix();

            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            float rearWheelSpin = entity.prevRearWheelRotation + (entity.rearWheelRotation - entity.prevRearWheelRotation) * partialTicks;

            double wheelScale = 1.95F;
            double offsetCenter = 0.65625;

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0.3, 0.13125, offsetCenter);
                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-0.6, 0, 0);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.rotate(wheelAngle, 0, 1, 0);
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
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
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
                    }

                    GlStateManager.translate(0.0625 * wheelScale - 0.0625, -0.5375 * wheelScale, 0.0);
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderManager().renderEntity(WHEEL, 0, 0, 0, 0f, 0f, true);
                }
                GlStateManager.popMatrix();

                GlStateManager.translate(-0.6, 0, 0);

                GlStateManager.pushMatrix();
                {
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-rearWheelSpin, 1, 0, 0);
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
