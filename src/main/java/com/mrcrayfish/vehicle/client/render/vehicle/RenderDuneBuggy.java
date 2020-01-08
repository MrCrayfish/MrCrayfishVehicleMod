package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.entity.vehicle.DuneBuggyEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderDuneBuggy extends AbstractRenderVehicle<DuneBuggyEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.DUNE_BUGGY_BODY;
    }

    @Override
    public void render(DuneBuggyEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModel.DUNE_BUGGY_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        double wheelScale = 1.0F;

        //Render the handles bars
        matrixStack.func_227860_a_();

        matrixStack.func_227861_a_(0.0, 0.0, 3.125 * 0.0625);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-22.5F));
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(turnRotation));
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(22.5F));
        matrixStack.func_227861_a_(0.0, 0.0, -0.2);

        this.renderDamagedPart(entity, SpecialModel.DUNE_BUGGY_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        if(entity.hasWheels())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(0.0, -0.355, 0.33);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-frontWheelSpin));
            }
            matrixStack.func_227862_a_((float) wheelScale, (float) wheelScale, (float) wheelScale);
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F));
            this.renderDamagedPart(entity, RenderUtil.getModel(ItemLookup.getWheel(entity)), matrixStack, renderTypeBuffer, light);
            matrixStack.func_227865_b_();
        }

        matrixStack.func_227865_b_();
    }

    @Override
    public void applyPlayerModel(DuneBuggyEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 8F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-50F - turnRotation);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-50F + turnRotation);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }
}
