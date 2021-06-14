package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ShoppingCartRenderer extends AbstractLandVehicleRenderer<ShoppingCartEntity>
{
    public ShoppingCartRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable ShoppingCartEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SHOPPING_CART_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerModel(ShoppingCartEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightArm.xRot = (float) Math.toRadians(-70F);
        model.rightArm.yRot = (float) Math.toRadians(5F);
        model.leftArm.xRot = (float) Math.toRadians(-70F);
        model.leftArm.yRot = (float) Math.toRadians(-5F);
        model.rightLeg.xRot = (float) Math.toRadians(-90F);
        model.rightLeg.yRot = (float) Math.toRadians(15F);
        model.leftLeg.xRot = (float) Math.toRadians(-90F);
        model.leftLeg.yRot = (float) Math.toRadians(-15F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SHOPPING_CART_BODY, parts, transforms);
        };
    }
}
