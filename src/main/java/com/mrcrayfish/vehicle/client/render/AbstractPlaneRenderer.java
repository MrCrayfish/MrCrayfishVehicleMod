package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractPlaneRenderer<T extends PlaneEntity> extends AbstractPoweredRenderer<T>
{
    protected final PropertyFunction<T, Float> propellerRotationProperty = new PropertyFunction<>(PlaneEntity::getPropellerRotation, 0F);
    protected final PropertyFunction<T, Float> flapAngleProperty = new PropertyFunction<>(PlaneEntity::getFlapAngle, 0F);
    protected final PropertyFunction<T, Float> elevatorAngleProperty = new PropertyFunction<>(PlaneEntity::getElevatorAngle, 0F);

    public AbstractPlaneRenderer(EntityType<T> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        float bodyPitch = this.bodyPitchProperty.get(vehicle, partialTicks);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(bodyPitch));

        float bodyRoll = this.bodyRollProperty.get(vehicle, partialTicks);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(bodyRoll));

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
