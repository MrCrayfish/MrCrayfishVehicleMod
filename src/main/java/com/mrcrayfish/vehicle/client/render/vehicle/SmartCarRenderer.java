package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SmartCarEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

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
            float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
            float maxSteeringAngle = this.vehiclePropertiesProperty.get(vehicle).getExtended(PoweredProperties.class).getMaxSteeringAngle();
            float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(steeringWheelRotation));
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

        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-80F - steeringWheelRotation);
        model.leftArm.xRot = (float) Math.toRadians(-80F + steeringWheelRotation);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.SMART_CAR_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    MatrixTransform.translate(0.0F, 0.2F, 0.3F),
                    MatrixTransform.rotate(Axis.POSITIVE_X, -67.5F),
                    MatrixTransform.translate(0.0F, -0.02F, 0.0F),
                    MatrixTransform.scale(0.9F));
            TransformHelper.createTransformListForPart(SpecialModels.TOW_BAR, parts, transforms,
                    MatrixTransform.rotate(Axis.POSITIVE_Y, 180F),
                    MatrixTransform.translate(0.0F, 0.5F, 1.35F));
            TransformHelper.createFuelFillerTransforms(ModEntities.SMART_CAR.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
