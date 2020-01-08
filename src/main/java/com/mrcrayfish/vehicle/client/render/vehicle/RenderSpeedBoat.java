package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.SpeedBoatEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderSpeedBoat extends AbstractRenderVehicle<SpeedBoatEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.SPEED_BOAT_BODY;
    }

    @Override
    public void render(SpeedBoatEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(entity, SpecialModel.SPEED_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0, 0.215, -0.125);
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-45F));
        matrixStack.func_227861_a_(0, 0.02, 0);
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(turnRotation));
        RenderUtil.renderColoredModel(SpecialModel.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.field_229196_a_);
        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(SpeedBoatEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);

        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
    }

    @Override
    public void applyPlayerRender(SpeedBoatEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        matrixStack.func_227861_a_(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
        matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_(turnAngleNormal * currentSpeedNormal * 15F));
        matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(-8F * Math.min(1.0F, currentSpeedNormal)));
        matrixStack.func_227861_a_(0, -offset, 0);
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}
