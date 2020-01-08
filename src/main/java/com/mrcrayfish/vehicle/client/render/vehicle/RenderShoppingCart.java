package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModel;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderShoppingCart extends AbstractRenderVehicle<ShoppingCartEntity>
{
    @Override
    public SpecialModel getBodyModel()
    {
        return SpecialModel.SHOPPING_CART_BODY;
    }

    @Override
    public void render(ShoppingCartEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity,SpecialModel.SHOPPING_CART_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerModel(ShoppingCartEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-70F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-70F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
    }
}
