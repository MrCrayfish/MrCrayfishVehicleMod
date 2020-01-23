package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.vehicle.JetSkiEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderJetSki extends AbstractRenderVehicle<JetSkiEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.JET_SKI_BODY;
    }

    @Override
    public void render(JetSkiEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(entity, SpecialModel.JET_SKI_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.push();

        matrixStack.translate(0, 0.355, 0.225);
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-45F));

        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(turnRotation));

        this.renderDamagedPart(entity, SpecialModel.ATV_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pop();
    }

    @Override
    public void applyPlayerModel(JetSkiEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / (float) entity.getMaxTurnAngle();
        float turnRotation = wheelAngleNormal * 12F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
        //model.bipedRightArm.offsetZ = -0.1F * wheelAngleNormal; //TODO test this out
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);
        //model.bipedLeftArm.offsetZ = 0.1F * wheelAngleNormal;

        if(entity.getControllingPassenger() != player)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
        }

        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(JetSkiEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        matrixStack.translate(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
        matrixStack.rotate(Axis.POSITIVE_Z.func_229187_a_(turnAngleNormal * currentSpeedNormal * 15F));
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-8F * Math.min(1.0F, currentSpeedNormal)));
        matrixStack.translate(0, -offset, 0);
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}
