package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.ATVEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ATVRenderer extends AbstractLandVehicleRenderer<ATVEntity>
{
    public ATVRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable ATVEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Body
        this.renderDamagedPart(vehicle, SpecialModels.ATV_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Handle bar transformations
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.3375, 0.25);
        matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-45F));
        matrixStack.translate(0.0, -0.025, 0);

        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(turnRotation));
        }

        RenderUtil.renderColoredModel(SpecialModels.ATV_HANDLES.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(ATVEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 12F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
        model.rightArm.yRot = (float) Math.toRadians(15F);
        model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
        model.leftArm.yRot = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() != player)
        {
            model.rightArm.xRot = (float) Math.toRadians(-20F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.rightArm.zRot = (float) Math.toRadians(15F);
            model.leftArm.xRot = (float) Math.toRadians(-20F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.zRot = (float) Math.toRadians(-15F);
            model.rightLeg.xRot = (float) Math.toRadians(-85F);
            model.rightLeg.yRot = (float) Math.toRadians(30F);
            model.leftLeg.xRot = (float) Math.toRadians(-85F);
            model.leftLeg.yRot = (float) Math.toRadians(-30F);
            return;
        }

        model.rightLeg.xRot = (float) Math.toRadians(-65F);
        model.rightLeg.yRot = (float) Math.toRadians(30F);
        model.leftLeg.xRot = (float) Math.toRadians(-65F);
        model.leftLeg.yRot = (float) Math.toRadians(-30F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.3375F, 0.25F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.025F, 0.0F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 1.05F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.ATV.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.ATV.get(), parts, transforms);
        };
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}
