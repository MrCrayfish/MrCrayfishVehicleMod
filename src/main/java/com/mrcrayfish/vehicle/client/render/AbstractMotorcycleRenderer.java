package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractMotorcycleRenderer<T extends MotorcycleEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractLandVehicleRenderer<T>
{
    public AbstractMotorcycleRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        if(vehicle != null)
        {
            // Rotates the vehicle based on the entity yaw
            float additionalYaw = vehicle.prevAdditionalYaw + (vehicle.additionalYaw - vehicle.prevAdditionalYaw) * partialTicks;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(additionalYaw));

            //Applies leaning rotation caused by turning
            float currentSpeedNormal = (vehicle.prevCurrentSpeed + (vehicle.currentSpeed - vehicle.prevCurrentSpeed) * partialTicks) / vehicle.getMaxSpeed();
            float turnAngleNormal = (vehicle.prevTurnAngle + (vehicle.turnAngle - vehicle.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * -20F));
        }

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Translate the vehicle to match how it is shown in the model creator
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        /* Rotates the wheel based relative to the rear axel to create a wheelie */
        if(vehicle != null && vehicle.canWheelie())
        {
            if(properties.getRearAxelVec() == null)
            {
                return;
            }
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
            float wheelieProgress = MathHelper.lerp(partialTicks, vehicle.prevWheelieCount, vehicle.wheelieCount) / 4F;
            wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F * wheelieProgress));
            matrixStack.translate(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
            matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.5, 0.0);
        }

        //Render body
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        //Render vehicle wheels
        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -8 * 0.0625, 0.0);
            matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
            IBakedModel wheelModel = RenderUtil.getModel(wheelStack);
            properties.getWheels().forEach(wheel -> this.renderWheel(vehicle, wheel, wheelStack, wheelModel, partialTicks, matrixStack, renderTypeBuffer, light));
            matrixStack.popPose();
        }

        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderSteeringDebug(matrixStack, properties, vehicle);

        matrixStack.popPose();
    }
}
