package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.VehicleModels;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractPlaneRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

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
        this.renderDamagedPart(vehicle, VehicleModels.SPORTS_PLANE_BODY, matrixStack, renderTypeBuffer, light, partialTicks);
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
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE, parts, transforms);
            TransformHelper.createFuelFillerTransforms(ModEntities.SPORTS_PLANE.get(), VehicleModels.FUEL_DOOR_CLOSED, parts, transforms);
            TransformHelper.createIgnitionTransforms(ModEntities.SPORTS_PLANE.get(), parts, transforms);
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_WING, parts, transforms,
                    MatrixTransform.translate(0, -0.1875F, 0.5F),
                    MatrixTransform.rotate(Axis.POSITIVE_Z.rotationDegrees(180F)),
                    MatrixTransform.translate(0.875F, 0.0625F, 0.0F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(5F)));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_WING, parts, transforms,
                    MatrixTransform.translate(0.875F, -0.1875F, 0.5F),
                    MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees(-5F)));
            transforms.add(MatrixTransform.translate(0.0F, -0.5F, 0.0F));
            transforms.add(MatrixTransform.scale(0.85F));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(0.0F, -0.1875F, 1.5F));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(0.0F, -0.1875F, 1.5F));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(-0.46875F, -0.1875F, 0.125F));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(-0.46875F, -0.1875F, 0.125F),
                    MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees(-100F)));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    MatrixTransform.translate(0.46875F, -0.1875F, 0.125F));
            TransformHelper.createTransformListForPart(VehicleModels.SPORTS_PLANE_LEG, parts, transforms,
                    MatrixTransform.translate(0.46875F, -0.1875F, 0.125F),
                    MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees(100F)));
        };
    }
}
