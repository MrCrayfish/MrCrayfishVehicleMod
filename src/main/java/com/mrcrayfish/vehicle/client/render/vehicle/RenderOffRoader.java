package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.OffRoaderEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderOffRoader extends AbstractRenderVehicle<OffRoaderEntity>
{
    @Override
    public void render(OffRoaderEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.OFF_ROADER_BODY.getModel());

        //Render the handles bars
        GlStateManager.pushMatrix();
            // Positions the steering wheel in the correct position
        GlStateManager.translated(-0.3125, 0.35, 0.2);
        GlStateManager.rotatef(-45F, 1, 0, 0);
        GlStateManager.translated(0, -0.02, 0);
        GlStateManager.scalef(0.75F, 0.75F, 0.75F);

        // Rotates the steering wheel based on the wheel angle
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;
        GlStateManager.rotatef(turnRotation, 0, 1, 0);

        RenderUtil.renderColoredModel(SpecialModels.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, -1);

        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(OffRoaderEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
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
