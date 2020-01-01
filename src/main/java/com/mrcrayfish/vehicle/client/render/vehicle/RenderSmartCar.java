package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.SmartCarEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderSmartCar extends AbstractRenderVehicle<SmartCarEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.SMART_CAR_BODY;
    }

    @Override
    public void render(SmartCarEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModel.SMART_CAR_BODY.getModel(), matrixStack, renderTypeBuffer);

        //Render the handles bars
        matrixStack.func_227860_a_();
        {
            matrixStack.func_227861_a_(0, 0.2, 0.3);
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-67.5F));
            matrixStack.func_227861_a_(0, -0.02, 0);
            matrixStack.func_227862_a_(0.9F, 0.9F, 0.9F);

            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(turnRotation));

            RenderUtil.renderColoredModel(SpecialModel.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.field_229196_a_);
        }
        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(SmartCarEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;

        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F - turnRotation);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F + turnRotation);
    }
}
