package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.GoKartEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SportsCarEntity;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SportsCarRenderer extends AbstractLandVehicleRenderer<SportsCarEntity>
{
    public SportsCarRenderer(EntityType<SportsCarEntity> type, VehicleProperties properties)
    {
        super(type, properties);
    }

    @Override
    protected void render(@Nullable SportsCarEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_CAR_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderSteeringWheel(vehicle, SpecialModels.SPORTS_CAR_STEERING_WHEEL.getModel(), -4.0, -1.0961, 1.6378, 0.7F, -67.5F, matrixStack, renderTypeBuffer, light, partialTicks);
    }

    @Override
    public void applyPlayerModel(SportsCarEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);

        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-75F - steeringWheelRotation);
        model.rightArm.yRot = (float) Math.toRadians(-7F);
        model.leftArm.xRot = (float) Math.toRadians(-75F + steeringWheelRotation);
        model.leftArm.yRot = (float) Math.toRadians(7F);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) -> {
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_CAR_BODY, parts, transforms);
        };
    }
}
