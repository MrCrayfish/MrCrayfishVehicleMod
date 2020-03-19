package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityCouch;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderCouch extends AbstractRenderVehicle<EntityCouch>
{
    @Override
    public void render(EntityCouch entity, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.0625, 0);
        GlStateManager.rotate(90F, 0, 1, 0);
        this.renderDamagedPart(entity,SpecialModels.COUCH.getModel());
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityCouch entity, EntityPlayer player, ModelPlayer model, float partialTicks)
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
