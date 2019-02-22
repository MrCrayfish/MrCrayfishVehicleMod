package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityOffRoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RenderOffRoader extends AbstractRenderLandVehicle<EntityOffRoader>
{
    public RenderOffRoader()
    {
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 14.5F, 2.25F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, -14.5F, 2.25F);
    }

    @Override
    public void render(EntityOffRoader entity, float partialTicks)
    {
        renderDamagedPart(entity, entity.body);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            // Positions the steering wheel in the correct position
            GlStateManager.translate(-0.3125, 0.35, 0.2);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.02, 0);
            GlStateManager.scale(0.75, 0.75, 0.75);

            // Rotates the steering wheel based on the wheel angle
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityOffRoader entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        List<Entity> passengers = entity.getPassengers();
        int index = passengers.indexOf(player);
        if(index < 2) //Sitting in the front
        {
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-80F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-80F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);

            if(index == 1)
            {
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-25F);
                model.bipedLeftArm.rotateAngleZ = 0F;
            }
        }
        else
        {
            if(index == 3)
            {
                model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
                model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
                model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
                model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F);
                model.bipedRightArm.rotateAngleY = (float) Math.toRadians(110F);
                model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(0F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-105F);
                model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-20F);
                model.bipedLeftArm.rotateAngleZ = 0F;
            }
            else
            {
                model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(0F);
                model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(0F);
                model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(0F);
                model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(0F);
                model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-10F);
                model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(25F);
                model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F);
                model.bipedLeftArm.rotateAngleZ = 0F;
                model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-20F);
                model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(20F);
            }
        }

        if(entity.getControllingPassenger() == player)
        {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
        }
    }
}
