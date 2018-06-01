package com.mrcrayfish.vehicle.client.render.vehicle;

import java.awt.Color;

import javax.annotation.Nullable;

import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityATV;
import com.mrcrayfish.vehicle.init.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends RenderLandVehicle<EntityATV>
{
    public RenderATV(RenderManager renderManager)
    {
        super(renderManager);
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.95F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.95F));
        wheels.add(new Wheel(Wheel.Side.LEFT, Wheel.Position.REAR, 4.0F, -10.5F, 1.95F));
        wheels.add(new Wheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 4.0F, -10.5F, 1.95F));
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
            GlStateManager.scale(1.25, 1.25, 1.25);
            GlStateManager.translate(0, -0.03125, 0.2);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyLevelToGround = 0.4375;
            double bodyOffset = 4.375 * 0.0625;

            //Render the body
            GlStateManager.pushMatrix();
            {
            	GlStateManager.translate(0, bodyLevelToGround + bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;

            //Render the handles bars
            GlStateManager.pushMatrix();
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
            GlStateManager.popMatrix();

            GlStateManager.translate(0, bodyOffset + 0.03125, 0);
            super.doRender(entity, x, y, z, currentYaw, partialTicks);
        }
        GlStateManager.popMatrix();
    }
}
