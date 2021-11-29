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
import com.mrcrayfish.vehicle.entity.vehicle.QuadBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class QuadBikeRenderer extends AbstractLandVehicleRenderer<QuadBikeEntity>
{
    public QuadBikeRenderer(EntityType<QuadBikeEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable QuadBikeEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.QUAD_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderSteeringWheel(vehicle, SpecialModels.QUAD_BIKE_HANDLES.getModel(), 0.0, 6.0, 3.0, 1.0F, -35F, matrixStack, renderTypeBuffer, light, partialTicks);
    }

    @Override
    public void applyPlayerModel(QuadBikeEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 15F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - steeringWheelRotation);
        model.rightArm.yRot = (float) Math.toRadians(15F);
        model.leftArm.xRot = (float) Math.toRadians(-65F + steeringWheelRotation);
        model.leftArm.yRot = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() != player)
        {
            model.rightArm.xRot = (float) Math.toRadians(-20F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.rightArm.zRot = (float) Math.toRadians(15F);
            model.leftArm.xRot = (float) Math.toRadians(-20F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.zRot = (float) Math.toRadians(-15F);
            model.rightLeg.xRot = (float) Math.toRadians(-85F);
            model.rightLeg.yRot = (float) Math.toRadians(30F);
            model.leftLeg.xRot = (float) Math.toRadians(-85F);
            model.leftLeg.yRot = (float) Math.toRadians(-30F);
            return;
        }

        model.rightLeg.xRot = (float) Math.toRadians(-45F);
        model.rightLeg.yRot = (float) Math.toRadians(40F);
        model.leftLeg.xRot = (float) Math.toRadians(-45F);
        model.leftLeg.yRot = (float) Math.toRadians(-40F);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.QUAD_BIKE_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.QUAD_BIKE_HANDLES, parts, transforms,
                    MatrixTransform.translate(0.0F, 6.0F, 3.0F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-35F)));
            TransformHelper.createTowBarTransforms(ModEntities.QUAD_BIKE.get(), SpecialModels.TOW_BAR, parts);
            TransformHelper.createFuelFillerTransforms(ModEntities.QUAD_BIKE.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            TransformHelper.createIgnitionTransforms(ModEntities.QUAD_BIKE.get(), parts, transforms);
        };
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}
