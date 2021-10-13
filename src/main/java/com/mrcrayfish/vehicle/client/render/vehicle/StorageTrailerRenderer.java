package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceTransforms;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.render.model.ChestModel;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class StorageTrailerRenderer extends AbstractTrailerRenderer<StorageTrailerEntity>
{
    private static final ChestModel CHEST = new ChestModel();

    public StorageTrailerRenderer(EntityType<StorageTrailerEntity> type, VehicleProperties properties)
    {
        super(type, properties);
    }

    @Override
    public void render(@Nullable StorageTrailerEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.STORAGE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();
        matrixStack.translate(0, -6 * 0.0625, 0);
        matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(180F));
        matrixStack.scale(0.9F, 0.9F, 0.9F);
        matrixStack.translate(-0.5, 0.0, -0.5);
        CHEST.render(matrixStack, renderTypeBuffer, Pair.of(0F, 0F), light, partialTicks);
        matrixStack.popPose();
    }

    @Nullable
    @Override
    public RayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            TransformHelper.createTransformListForPart(SpecialModels.STORAGE_TRAILER, parts, transforms);
            TransformHelper.createTowBarTransforms(ModEntities.STORAGE_TRAILER.get(), SpecialModels.TOW_BAR, parts);
        };
    }
}
