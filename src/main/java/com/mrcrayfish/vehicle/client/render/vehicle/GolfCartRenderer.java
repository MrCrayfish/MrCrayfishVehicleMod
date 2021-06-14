package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.GolfCartEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class GolfCartRenderer extends AbstractLandVehicleRenderer<GolfCartEntity>
{
    public GolfCartRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable GolfCartEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(vehicle, SpecialModels.GOLF_CART_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        // Positions the steering wheel in the correct position
        matrixStack.translate(-0.345, 0.425, 0.1);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.translate(0, -0.02, 0);
        matrixStack.scale(0.95F, 0.95F, 0.95F);

        // Rotates the steering wheel based on the wheel angle
        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(turnRotation));
        }

        this.renderDamagedPart(vehicle, SpecialModels.GO_KART_STEERING_WHEEL.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(GolfCartEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-80F);
        model.rightLeg.yRot = (float) Math.toRadians(15F);
        model.leftLeg.xRot = (float) Math.toRadians(-80F);
        model.leftLeg.yRot = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() == player)
        {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
            model.rightArm.yRot = (float) Math.toRadians(-7F);
            model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
            model.leftArm.yRot = (float) Math.toRadians(7F);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.GOLF_CART_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(-0.345F, 0.425F, 0.1F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createScale(0.95F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.GOLF_CART.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.GOLF_CART.get(), parts, transforms);
        };
    }
}
