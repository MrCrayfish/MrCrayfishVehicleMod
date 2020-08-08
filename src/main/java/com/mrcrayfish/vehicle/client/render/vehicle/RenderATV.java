package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.ATVEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends AbstractRenderVehicle<ATVEntity>
{
    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }

    @Override
    public void render(ATVEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Body
        this.renderDamagedPart(entity, SpecialModels.ATV_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Handle bar transformations
        matrixStack.push();
        matrixStack.translate(0.0, 0.3375, 0.25);
        matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.translate(0.0, -0.025, 0);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        matrixStack.rotate(Axis.POSITIVE_Y.rotationDegrees(turnRotation));

        RenderUtil.renderColoredModel(SpecialModels.ATV_HANDLES.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);

        matrixStack.pop();
    }

    @Override
    public void applyPlayerModel(ATVEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
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
