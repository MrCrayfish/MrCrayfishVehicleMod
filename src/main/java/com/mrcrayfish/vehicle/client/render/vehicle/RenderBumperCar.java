package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderBumperCar extends AbstractRenderVehicle<BumperCarEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.BUMPER_CAR_BODY;
    }

    @Override
    public void render(BumperCarEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        //Render body
        this.renderDamagedPart(entity, SpecialModel.BUMPER_CAR_BODY.getModel(), matrixStack, renderTypeBuffer);

        //Render the handles bars
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0, 0.2, 0);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-45F));
        matrixStack.func_227861_a_(0, -0.02, 0);
        matrixStack.func_227862_a_(0.9F, 0.9F, 0.9F);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(turnRotation));

        RenderUtil.renderColoredModel(SpecialModel.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, entity.getColor(), 15728880, OverlayTexture.field_229196_a_);

        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(BumperCarEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;

        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
    }
}
