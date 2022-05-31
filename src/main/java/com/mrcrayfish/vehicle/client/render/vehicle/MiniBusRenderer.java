package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.IVehicleModel;
import com.mrcrayfish.vehicle.client.model.VehicleModels;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBusEntity;
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
public class MiniBusRenderer extends AbstractLandVehicleRenderer<MiniBusEntity>
{
    public MiniBusRenderer(EntityType<MiniBusEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable MiniBusEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, VehicleModels.MINI_BUS_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderSteeringWheel(vehicle, VehicleModels.MINI_BUS_STEERING_WHEEL.getModel(), -5.0, 4.9039, 15.6378, 0.7F, -67.5F, matrixStack, renderTypeBuffer, light, partialTicks);
    }

    @Override
    public void applyPlayerModel(MiniBusEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        if(entity.getControllingPassenger() == player)
        {
            float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
            float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
            float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
            model.rightArm.xRot = (float) Math.toRadians(-75F - steeringWheelRotation);
            model.rightArm.yRot = (float) Math.toRadians(-7F);
            model.leftArm.xRot = (float) Math.toRadians(-75F + steeringWheelRotation);
            model.leftArm.yRot = (float) Math.toRadians(7F);
        }
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(VehicleModels.MINI_BUS_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(VehicleModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    MatrixTransform.translate(-0.2825F, 0.225F, 1.0625F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-67.5F)),
                    MatrixTransform.translate(0.0F, -0.02F, 0.0F),
                    MatrixTransform.scale(0.75F));
            TransformHelper.createTowBarTransforms(ModEntities.MINI_BUS.get(), VehicleModels.BIG_TOW_BAR, parts);
            TransformHelper.createFuelFillerTransforms(ModEntities.MINI_BUS.get(), VehicleModels.FUEL_DOOR_CLOSED, parts, transforms);
            TransformHelper.createIgnitionTransforms(ModEntities.MINI_BUS.get(), parts, transforms);
        };
    }

    @Override
    public IVehicleModel getTowBarModel()
    {
        return VehicleModels.BIG_TOW_BAR;
    }
}
