package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityGolfCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderGolfCart extends AbstractRenderLandVehicle<EntityGolfCart>
{
    @Override
    public void render(EntityGolfCart entity, float partialTicks)
    {
        //Render the body
        renderDamagedPart(entity, entity.body);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            // Positions the steering wheel in the correct position
            GlStateManager.translate(-0.345, 0.425, 0.1);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.02, 0);
            GlStateManager.scale(0.95, 0.95, 0.95);

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
    public void applyPlayerModel(EntityGolfCart entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);

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
