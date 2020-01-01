package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.ATVEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
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
    public SpecialModel getBodyModel()
    {
        return SpecialModel.ATV_BODY;
    }

    @Override
    public void render(ATVEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks)
    {
        //Body
        this.renderDamagedPart(entity, SpecialModel.ATV_BODY.getModel(), matrixStack, renderTypeBuffer);

        //Handle bar transformations
        matrixStack.func_227860_a_();
        matrixStack.func_227861_a_(0.0, 0.3375, 0.25);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-45F));
        matrixStack.func_227861_a_(0.0, -0.025, 0.25);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(turnRotation));

        RenderUtil.renderColoredModel(SpecialModel.ATV_HANDLES.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, entity.getColor(), 15728880, OverlayTexture.field_229196_a_);

        matrixStack.func_227865_b_();
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
