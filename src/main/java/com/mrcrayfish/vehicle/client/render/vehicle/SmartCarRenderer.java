package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SmartCarEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SmartCarRenderer extends AbstractLandVehicleRenderer<SmartCarEntity>
{
    public SmartCarRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable SmartCarEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SMART_CAR_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();
        {
            matrixStack.translate(0, 0.2, 0.3);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-67.5F));
            matrixStack.translate(0, -0.02, 0);
            matrixStack.scale(0.9F, 0.9F, 0.9F);

            if(vehicle != null)
            {
                float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(turnRotation));
            }

            RenderUtil.renderColoredModel(SpecialModels.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        }
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(SmartCarEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;

        model.rightArm.xRot = (float) Math.toRadians(-80F - turnRotation);
        model.leftArm.xRot = (float) Math.toRadians(-80F + turnRotation);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SMART_CAR_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.2F, 0.3F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 1.35F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.SMART_CAR.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
