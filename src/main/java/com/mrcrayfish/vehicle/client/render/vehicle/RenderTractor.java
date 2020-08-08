package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.TractorEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderTractor extends AbstractRenderVehicle<TractorEntity>
{
    @Override
    public void render(TractorEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.TRACTOR.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.push();
        matrixStack.translate(0, 0.66, -0.475);
        matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-67.5F));
        matrixStack.translate(0, -0.02, 0);
        matrixStack.scale(0.9F, 0.9F, 0.9F);
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;
        matrixStack.rotate(Axis.POSITIVE_Y.rotationDegrees(turnRotation));
        this.renderDamagedPart(entity, SpecialModels.GO_KART_STEERING_WHEEL.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pop();
    }

    @Override
    public void applyPlayerModel(TractorEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-75F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-75F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;

        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-10F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(10F);
    }
}
