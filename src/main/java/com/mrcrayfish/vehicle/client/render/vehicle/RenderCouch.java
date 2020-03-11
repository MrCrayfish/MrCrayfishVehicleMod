package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.CouchEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends AbstractRenderVehicle<CouchEntity>
{
    @Override
    public void render(CouchEntity entity, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated(0.0, 0.0625, 0.0);
        //GlStateManager.rotatef(Vector3f.field_229181_d_.func_229187_a_(90F));
        this.renderDamagedPart(entity, SpecialModels.RAINBOW_SOFA.getModel());
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(CouchEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(25F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-25F);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-90F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-15F);
    }
}
