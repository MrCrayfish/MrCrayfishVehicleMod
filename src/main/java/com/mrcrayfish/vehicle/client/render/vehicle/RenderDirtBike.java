package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.entity.vehicle.DirtBikeEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Author: MrCrayfish
 */
public class RenderDirtBike extends AbstractRenderVehicle<DirtBikeEntity>
{
    @Override
    public void render(DirtBikeEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.push();

        matrixStack.translate(0.0, 0.0, 10.5 * 0.0625);
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-22.5F));

        float wheelScale = 1.65F;
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(turnRotation));
        matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(22.5F));
        matrixStack.translate(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        if(entity.hasWheels())
        {
            matrixStack.push();
            matrixStack.translate(0, -0.5 + 1.7 * 0.0625, 13 * 0.0625);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                matrixStack.rotate(Axis.POSITIVE_X.func_229187_a_(-frontWheelSpin));
            }
            matrixStack.scale(wheelScale, wheelScale, wheelScale);
            matrixStack.rotate(Axis.POSITIVE_Y.func_229187_a_(180F));
            //RenderUtil.renderColoredModel(RenderUtil.getModel(ItemLookup.getWheel(entity)), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.DEFAULT_LIGHT);
            matrixStack.pop();
        }

        matrixStack.pop();
    }
}
