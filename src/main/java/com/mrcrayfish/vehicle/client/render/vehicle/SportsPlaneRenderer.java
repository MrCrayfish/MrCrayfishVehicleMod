package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractPlaneRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SportsPlaneRenderer extends AbstractPlaneRenderer<SportsPlaneEntity>
{
    public SportsPlaneRenderer(EntityType<SportsPlaneEntity> type, VehicleProperties defaultProperties)
    {
        super(type, defaultProperties);
    }

    @Override
    protected void render(@Nullable SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.translate(8 * 0.0625, 0, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-5F));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(180F));
            matrixStack.translate(8 * 0.0625, 0.0625, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(5F));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -0.5, 0);
            matrixStack.scale(0.85F, 0.85F, 0.85F);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks, light, true);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks, light, false);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks, light, false);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -1.5 * 0.0625, 22.2 * 0.0625);
            float propellerRotation = this.propellerRotationProperty.get(vehicle, partialTicks);
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(propellerRotation));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_PROPELLER.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }

    private void renderPlaneLeg(@Nullable SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks, int light, boolean rotate)
    {
        matrixStack.pushPose();
        {
            matrixStack.translate(offsetX, offsetY, offsetZ);

            matrixStack.pushPose();
            if(rotate)
            {
                float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(wheelAngle));
            }
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WHEEL_COVER.getModel(), matrixStack, renderTypeBuffer, light);
            matrixStack.popPose();

            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(legRotation));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_LEG.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(SportsPlaneEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE, parts, transforms);
            TransformHelper.createFuelFillerTransforms(ModEntities.SPORTS_PLANE.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            TransformHelper.createIgnitionTransforms(ModEntities.SPORTS_PLANE.get(), parts, transforms);
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                    MatrixTransform.translate(0, -0.1875F, 0.5F),
                    MatrixTransform.rotate(Axis.POSITIVE_Z.rotationDegrees(180F)),
                    MatrixTransform.translate(0.875F, 0.0625F, 0.0F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(5F)));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                    MatrixTransform.translate(0.875F, -0.1875F, 0.5F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-5F)));
            transforms.add(MatrixTransform.translate(0.0F, -0.5F, 0.0F));
            transforms.add(MatrixTransform.scale(0.85F));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(0.0F, -0.1875F, 1.5F));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(0.0F, -0.1875F, 1.5F));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(-0.46875F, -0.1875F, 0.125F));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(-0.46875F, -0.1875F, 0.125F),
                    MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees(-100F)));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(0.46875F, -0.1875F, 0.125F));
            TransformHelper.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(0.46875F, -0.1875F, 0.125F),
                    MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees(100F)));
        };
    }
}
