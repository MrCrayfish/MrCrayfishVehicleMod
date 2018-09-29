package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityATV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends AbstractRenderLandVehicle<EntityATV>
{
    public RenderATV()
    {
        this.setAxleOffset(-1.5F);
        this.setWheelOffset(4.375F);
        this.setFuelPortPosition(-1.57F, 6.55F, 5.3F, -90.0F, 0.0F, 0.0F, 0.35F);
        this.setKeyPortPosition(-5F, 4.5F, 6.5F, -45.0F, 0.0F, 0.0F, 0.5F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.85F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 4.0F, -10.5F, 1.85F);
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }

    @Override
    public void render(EntityATV entity, float partialTicks)
    {
        //Body
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        //Handle Bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0.3375, 0.25);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.025, 0);

            float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.handleBar, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityATV entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 12F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() != player)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(15F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(-15F);
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
            return;
        }

        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }
}
