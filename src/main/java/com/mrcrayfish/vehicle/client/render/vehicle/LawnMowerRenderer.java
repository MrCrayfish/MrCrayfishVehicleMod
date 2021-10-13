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
import com.mrcrayfish.vehicle.entity.vehicle.LawnMowerEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class LawnMowerRenderer extends AbstractLandVehicleRenderer<LawnMowerEntity>
{
    public LawnMowerRenderer(EntityType<LawnMowerEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable LawnMowerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Body
        this.renderDamagedPart(vehicle, SpecialModels.LAWN_MOWER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0, 0.4, -0.15);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.scale(0.9F, 0.9F, 0.9F);

        float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(vehicle).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(steeringWheelRotation));

        this.renderDamagedPart(vehicle, SpecialModels.GO_KART_STEERING_WHEEL.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(LawnMowerEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-55F - steeringWheelRotation);
        model.rightArm.yRot = (float) Math.toRadians(-7F);
        model.leftArm.xRot = (float) Math.toRadians(-55F + steeringWheelRotation);
        model.leftArm.yRot = (float) Math.toRadians(7F);
        model.rightLeg.xRot = (float) Math.toRadians(-65F);
        model.rightLeg.yRot = (float) Math.toRadians(20F);
        model.leftLeg.xRot = (float) Math.toRadians(-65F);
        model.leftLeg.yRot = (float) Math.toRadians(-20F);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.LAWN_MOWER_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    MatrixTransform.translate(0.0F, 0.4F, -0.15F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-45F)),
                    MatrixTransform.scale(0.9F));
            TransformHelper.createTowBarTransforms(ModEntities.LAWN_MOWER.get(), SpecialModels.TOW_BAR, parts);
            TransformHelper.createFuelFillerTransforms(ModEntities.LAWN_MOWER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
