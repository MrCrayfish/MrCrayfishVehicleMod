package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class MiniBikeRenderer extends AbstractMotorcycleRenderer<MiniBikeEntity>
{
    public MiniBikeRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable MiniBikeEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.MINI_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0.0, 0.0, 10.5 * 0.0625);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-22.5F));
        float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(vehicle).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(steeringWheelRotation));
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(22.5F));
        matrixStack.translate(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(vehicle, SpecialModels.MINI_BIKE_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getFirstFrontWheel();
            if(wheel != null)
            {
                matrixStack.pushPose();
                matrixStack.translate(0, -0.5 + 1.7 * 0.0625, wheel.getOffsetZ() * 0.0625);
                if(vehicle != null)
                {
                    float frontWheelSpin = vehicle.getFrontWheelRotation(partialTicks);
                    if(vehicle.isMoving())
                    {
                        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-frontWheelSpin));
                    }
                }
                matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
                int wheelColor = IDyeable.getColorFromStack(wheelStack);
                RenderUtil.renderColoredModel(RenderUtil.getModel(wheelStack), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
        }

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(MiniBikeEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = this.wheelAngleProperty.get(entity, partialTicks);
        float maxSteeringAngle = this.vehiclePropertiesProperty.get(entity).getExtended(PoweredProperties.class).getMaxSteeringAngle();
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 25F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-55F - steeringWheelRotation);
        model.leftArm.xRot = (float) Math.toRadians(-55F + steeringWheelRotation);
        //model.bipedRightArm.offsetZ = -0.1F * wheelAngleNormal;
        //model.bipedLeftArm.offsetZ = 0.1F * wheelAngleNormal;
        model.rightLeg.xRot = (float) Math.toRadians(-65F);
        model.rightLeg.yRot = (float) Math.toRadians(30F);
        model.leftLeg.xRot = (float) Math.toRadians(-65F);
        model.leftLeg.yRot = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(MiniBikeEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        /*int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = -seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.steeringAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }*/
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.MINI_BIKE_BODY, parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.MINI_BIKE_HANDLES, parts, transforms);
            TransformHelper.createPartTransforms(ModItems.IRON_SMALL_ENGINE.get(), VehicleProperties.get(ModEntities.MINI_BIKE.get()).getExtended(PoweredProperties.class).getEngineTransform(), parts, transforms, RayTraceFunction.FUNCTION_FUELING);
        };
    }
}
