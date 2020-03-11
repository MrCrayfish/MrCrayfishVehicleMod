package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderShoppingCart extends AbstractRenderVehicle<ShoppingCartEntity>
{
    @Override
    public void render(ShoppingCartEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.SHOPPING_CART_BODY.getModel());
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
