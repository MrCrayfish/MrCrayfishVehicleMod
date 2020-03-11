package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.DuneBuggyEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderDuneBuggy extends AbstractRenderVehicle<DuneBuggyEntity>
{
    @Override
    public void render(DuneBuggyEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.DUNE_BUGGY_BODY.getModel());

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        double wheelScale = 1.0F;

        //Render the handles bars
        GlStateManager.pushMatrix();

        GlStateManager.translated(0.0, 0.0, 3.125 * 0.0625);
        GlStateManager.rotatef(-22.5F, 1, 0, 0);
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 15F;
        GlStateManager.rotatef(turnRotation, 0, 1, 0);
        GlStateManager.rotatef(22.5F, 1, 0, 0);
        GlStateManager.translated(0.0, 0.0, -0.2);

        this.renderDamagedPart(entity, SpecialModels.DUNE_BUGGY_HANDLES.getModel());

        if(entity.hasWheels())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0.0, -0.355, 0.33);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                GlStateManager.rotatef(-frontWheelSpin, 1, 0, 0);
            }
            GlStateManager.scalef((float) wheelScale, (float) wheelScale, (float) wheelScale);
            GlStateManager.rotatef(180F, 0, 1, 0);
            IBakedModel wheelModel = RenderUtil.getWheelModel(entity);
            RenderUtil.renderColoredModel(wheelModel, ItemCameraTransforms.TransformType.NONE, false, entity.getWheelColor());
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
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
