package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.OffRoaderEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RenderOffRoader extends AbstractRenderVehicle<OffRoaderEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.OFF_ROADER_BODY;
    }

    @Override
    public void render(OffRoaderEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.OFF_ROADER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.func_227860_a_();
            // Positions the steering wheel in the correct position
        matrixStack.func_227861_a_(-0.3125, 0.35, 0.2);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-45F));
        matrixStack.func_227861_a_(0, -0.02, 0);
        matrixStack.func_227862_a_(0.75F, 0.75F, 0.75F);

        // Rotates the steering wheel based on the wheel angle
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(turnRotation));

        RenderUtil.renderColoredModel(SpecialModel.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);

        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(OffRoaderEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
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
