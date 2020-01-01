package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.TRACTOR;
    }

    @Override
    public void render(TractorEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModel.TRACTOR.getModel(), matrixStack, renderTypeBuffer);

        //Render the handles bars
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0, 0.66, -0.475);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-67.5F));
        matrixStack.func_227861_a_(0, -0.02, 0);
        matrixStack.func_227862_a_(0.9F, 0.9F, 0.9F);
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(turnRotation));
        this.renderDamagedPart(entity, SpecialModel.GO_KART_STEERING_WHEEL.getModel(), matrixStack, renderTypeBuffer);
        matrixStack.func_227865_b_();
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
