package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractBoatRenderer<T extends BoatEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractPoweredRenderer<T>
{
    public AbstractBoatRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees((float) bodyPosition.getRotZ()));

        if(vehicle != null)
        {
            //Applies leaning rotation caused by turning
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * partialTicks) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * partialTicks) / vehicle.getMaxTurnAngle();
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(turnAngleNormal * currentSpeedNormal * -15F));

            //Makes the boat tilt up the faster it goes
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-8F * Math.min(1.0F, currentSpeedNormal)));
        }

        //this.renderRotationLine(matrixStack, 0xFF0000);

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Translate the vehicle to match how it is shown in the model creator
        matrixStack.translate(0.0, 0.5, 0.0);

        //Apply vehicle scale
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        //Render body
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }
}
