package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.LawnMowerEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class LawnMowerRenderer extends AbstractLandVehicleRenderer<LawnMowerEntity>
{
    public LawnMowerRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable LawnMowerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Body
        this.renderDamagedPart(vehicle, SpecialModels.LAWN_MOWER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0, 0.4, -0.15);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.scale(0.9F, 0.9F, 0.9F);

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
    public void applyPlayerModel(LawnMowerEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.rightArm.xRot = (float) Math.toRadians(-55F - turnRotation);
        model.rightArm.yRot = (float) Math.toRadians(-7F);
        model.leftArm.xRot = (float) Math.toRadians(-55F + turnRotation);
        model.leftArm.yRot = (float) Math.toRadians(7F);
        model.rightLeg.xRot = (float) Math.toRadians(-65F);
        model.rightLeg.yRot = (float) Math.toRadians(20F);
        model.leftLeg.xRot = (float) Math.toRadians(-65F);
        model.leftLeg.yRot = (float) Math.toRadians(-20F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.LAWN_MOWER_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.4F, -0.15F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                    EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 0.6F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.LAWN_MOWER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
