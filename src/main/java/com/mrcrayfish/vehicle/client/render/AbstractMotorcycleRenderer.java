package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.entity.properties.LandProperties;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractMotorcycleRenderer<T extends MotorcycleEntity> extends AbstractLandVehicleRenderer<T>
{
    public AbstractMotorcycleRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(this.bodyRollProperty.get(vehicle, partialTicks)));

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        Transform bodyPosition = properties.getBodyTransform();
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(bodyPosition.getX() * 0.0625, bodyPosition.getY() * 0.0625, bodyPosition.getZ() * 0.0625);

        // Fixes the origin
        matrixStack.translate(0.0, 0.5, 0.0);

        // Translate the vehicle so the center of the axles are touching the ground
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        // Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        /* Rotates the wheel based relative to the rear axel to create a wheelie */
        if(properties.getExtended(LandProperties.class).canWheelie())
        {
            Vector3d rearAxleOffset = properties.getExtended(PoweredProperties.class).getRearAxleOffset();
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.0, rearAxleOffset.z * 0.0625);
            float p = this.wheelieProgressProperty.get(vehicle, partialTicks);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F * this.boostStrengthProperty.get(vehicle) * p));
            matrixStack.translate(0.0, 0.0, -rearAxleOffset.z * 0.0625);
            matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.5, 0.0);
        }

        //Render body
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        matrixStack.popPose();

        this.renderWheels(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelFiller(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderIgnition(vehicle, matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }
}
