package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityDuneBuggy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderDuneBuggy extends AbstractRenderLandVehicle<EntityDuneBuggy>
{
    public RenderDuneBuggy()
    {
        this.setFuelPortPosition(EntityDuneBuggy.FUEL_PORT_POSITION);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, -4.25F, -5.7F, 1.0F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, -4.25F, -5.7F, 1.0F);
    }

    @Override
    public void render(EntityDuneBuggy entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        double wheelScale = 1.0F;

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0, 3.125 * 0.0625);
            GlStateManager.rotate(-22.5F, 1, 0, 0);
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);
            GlStateManager.rotate(22.5F, 1, 0, 0);
            GlStateManager.translate(0, 0, -0.2);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.handleBar, ItemCameraTransforms.TransformType.NONE);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -0.355, 0.33);
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
    }

    @Override
    public void applyPlayerModel(EntityDuneBuggy entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 8F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-50F - turnRotation);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-50F + turnRotation);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }
}
