package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.GoKartEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class GoKartRenderer extends AbstractLandVehicleRenderer<GoKartEntity>
{
    public GoKartRenderer(EntityType<GoKartEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable GoKartEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.GO_KART_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();
        matrixStack.translate(0, 0.09, 0.49);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-45F));
        matrixStack.translate(0, -0.02, 0);
        matrixStack.scale(0.9F, 0.9F, 0.9F);

        float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(vehicle).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(steeringWheelRotation));

        this.renderDamagedPart(vehicle, SpecialModels.GO_KART_STEERING_WHEEL.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(GoKartEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);

        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - steeringWheelRotation);
        model.rightArm.yRot = (float) Math.toRadians(-7F);
        model.leftArm.xRot = (float) Math.toRadians(-65F + steeringWheelRotation);
        model.leftArm.yRot = (float) Math.toRadians(7F);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.GO_KART_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    MatrixTransform.translate(0.0F, 0.09F, 0.49F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-45F)),
                    MatrixTransform.translate(0.0F, -0.02F, 0.0F),
                    MatrixTransform.scale(0.9F));
            TransformHelper.createEngineTransforms(ModItems.IRON_SMALL_ENGINE.get(), ModEntities.GO_KART.get(), parts, transforms, RayTraceFunction.FUNCTION_FUELING);
        };
    }
}
