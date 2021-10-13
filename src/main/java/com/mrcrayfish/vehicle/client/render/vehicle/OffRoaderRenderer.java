package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.OffRoaderEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class OffRoaderRenderer extends AbstractLandVehicleRenderer<OffRoaderEntity>
{
    public OffRoaderRenderer(EntityType<OffRoaderEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable OffRoaderEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.OFF_ROADER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();
            // Positions the steering wheel in the correct position
        matrixStack.translate(-0.3125, 0.35, 0.2);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.translate(0, -0.02, 0);
        matrixStack.scale(0.75F, 0.75F, 0.75F);

        float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(vehicle).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(steeringWheelRotation));

        RenderUtil.renderColoredModel(SpecialModels.GO_KART_STEERING_WHEEL.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(OffRoaderEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index < 2) //Sitting in the front
        {
            model.rightLeg.xRot = (float) Math.toRadians(-80F);
            model.rightLeg.yRot = (float) Math.toRadians(15F);
            model.leftLeg.xRot = (float) Math.toRadians(-80F);
            model.leftLeg.yRot = (float) Math.toRadians(-15F);

            if(index == 1)
            {
                model.leftArm.xRot = (float) Math.toRadians(-75F);
                model.leftArm.yRot = (float) Math.toRadians(-25F);
                model.leftArm.zRot = 0F;
            }
        }
        else
        {
            if(index == 3)
            {
                model.rightLeg.xRot = (float) Math.toRadians(-90F);
                model.rightLeg.yRot = (float) Math.toRadians(15F);
                model.leftLeg.xRot = (float) Math.toRadians(-90F);
                model.leftLeg.yRot = (float) Math.toRadians(-15F);
                model.rightArm.xRot = (float) Math.toRadians(-75F);
                model.rightArm.yRot = (float) Math.toRadians(110F);
                model.rightArm.zRot = (float) Math.toRadians(0F);
                model.leftArm.xRot = (float) Math.toRadians(-105F);
                model.leftArm.yRot = (float) Math.toRadians(-20F);
                model.leftArm.zRot = 0F;
            }
            else
            {
                model.rightLeg.xRot = (float) Math.toRadians(0F);
                model.rightLeg.yRot = (float) Math.toRadians(0F);
                model.leftLeg.xRot = (float) Math.toRadians(0F);
                model.leftLeg.yRot = (float) Math.toRadians(0F);
                model.rightArm.xRot = (float) Math.toRadians(-10F);
                model.rightArm.zRot = (float) Math.toRadians(25F);
                model.leftArm.xRot = (float) Math.toRadians(-80F);
                model.leftArm.zRot = 0F;
                model.leftLeg.xRot = (float) Math.toRadians(-20F);
                model.rightLeg.xRot = (float) Math.toRadians(20F);
            }
        }

        if(entity.getControllingPassenger() == player)
        {
            float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
            float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
            float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
            model.rightArm.xRot = (float) Math.toRadians(-65F - steeringWheelRotation);
            model.rightArm.yRot = (float) Math.toRadians(-7F);
            model.leftArm.xRot = (float) Math.toRadians(-65F + steeringWheelRotation);
            model.leftArm.yRot = (float) Math.toRadians(7F);
        }
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.OFF_ROADER_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    MatrixTransform.translate(-0.3125F, 0.35F, 0.2F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-45F)),
                    MatrixTransform.translate(0.0F, -0.02F, 0.0F),
                    MatrixTransform.scale(0.75F));
            TransformHelper.createFuelFillerTransforms(ModEntities.OFF_ROADER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            TransformHelper.createIgnitionTransforms(ModEntities.OFF_ROADER.get(), parts, transforms);
        };
    }
}
