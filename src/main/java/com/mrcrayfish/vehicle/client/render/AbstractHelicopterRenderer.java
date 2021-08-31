package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractHelicopterRenderer<T extends HelicopterEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractPoweredRenderer<T>
{
    public AbstractHelicopterRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        if(vehicle != null)
        {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(vehicle.getBodyRotationPitch(partialTicks)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(vehicle.getBodyRotationRoll(partialTicks)));
        }

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        Transform bodyPosition = properties.getBodyTransform();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0, 0.5, 0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0, properties.getAxleOffset() * 0.0625F, 0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0, properties.getWheelOffset() * 0.0625F, 0);

        //Render body
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        this.renderWheels(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPreRotations(T entity, MatrixStack matrixStack, float partialTicks)
    {

    }

    @Override
    public void applyPlayerRender(T entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyTransform().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double playerScale = 32.0 / 30.0;
            double offsetX = -seatVec.x * playerScale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * playerScale + (24 * 0.0625);
            double offsetZ = seatVec.z * playerScale;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-seat.getYawOffset()));
            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(entity.getBodyRotationPitch(partialTicks)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-entity.getBodyRotationRoll(partialTicks)));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(seat.getYawOffset()));
        }
    }
}
