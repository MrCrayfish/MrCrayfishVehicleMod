package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.DuneBuggyEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class DuneBuggyRenderer extends AbstractLandVehicleRenderer<DuneBuggyEntity>
{
    public DuneBuggyRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable DuneBuggyEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.DUNE_BUGGY_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0.0, 0.0, 3.125 * 0.0625);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
        if(vehicle != null)
        {
            float wheelAngle = MathHelper.lerp(partialTicks, vehicle.prevWheelAngle, vehicle.wheelAngle);
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(22.5F));
        matrixStack.translate(0.0, 0.0, -0.2);

        this.renderDamagedPart(vehicle, SpecialModels.DUNE_BUGGY_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getFirstFrontWheel();
            if(wheel != null)
            {
                matrixStack.pushPose();
                matrixStack.translate(0.0, -0.355, 0.33);
                if(vehicle != null)
                {
                    float frontWheelSpin = MathHelper.lerp(partialTicks, vehicle.prevFrontWheelRotation, vehicle.frontWheelRotation);
                    if(vehicle.isMoving())
                    {
                        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-frontWheelSpin));
                    }
                }
                matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
                int wheelColor = IDyeable.getColorFromStack(wheelStack);
                RenderUtil.renderColoredModel(RenderUtil.getModel(wheelStack), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
        }

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(DuneBuggyEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 8F;
        model.rightArm.xRot = (float) Math.toRadians(-50F - turnRotation);
        model.leftArm.xRot = (float) Math.toRadians(-50F + turnRotation);
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
            EntityRayTracer.createTransformListForPart(SpecialModels.DUNE_BUGGY_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.DUNE_BUGGY_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.0F, -0.0046875F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.DUNE_BUGGY.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
