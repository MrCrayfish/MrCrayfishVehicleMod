package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractHelicopterRenderer<T extends HelicopterEntity> extends AbstractPoweredRenderer<T>
{
    protected final PropertyFunction<T, Float> bladeRotationProperty = new PropertyFunction<>(HelicopterEntity::getBladeRotation, 0F);

    public AbstractHelicopterRenderer(EntityType<T> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(this.bodyPitchProperty.get(vehicle, partialTicks)));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(this.bodyRollProperty.get(vehicle, partialTicks)));

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        Transform bodyPosition = properties.getBodyTransform();
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(bodyPosition.getX() * 0.0625, bodyPosition.getY() * 0.0625, bodyPosition.getZ() * 0.0625);

        // Fixes the origin
        matrixStack.translate(0, 0.5, 0);

        // Translate the vehicle so the center of the axles are touching the ground
        matrixStack.translate(0, properties.getAxleOffset() * 0.0625F, 0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0, properties.getWheelOffset() * 0.0625F, 0);

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
