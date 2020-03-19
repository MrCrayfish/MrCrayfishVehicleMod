package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityATV;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderATV extends AbstractRenderVehicle<EntityATV>
{
    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }

    @Override
    public void render(EntityATV entity, float partialTicks)
    {
        //Body
        this.renderDamagedPart(entity, SpecialModels.ATV_BODY.getModel());

        //Handle Bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0.3375, 0.25);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.translate(0, -0.025, 0);

            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            RenderUtil.renderColoredModel(SpecialModels.ATV_HANDLE_BAR.getModel(), ItemCameraTransforms.TransformType.NONE, -1);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityATV entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 12F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-65F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(15F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-65F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() != player)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedRightArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(15F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-20F);
            model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(0F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(-15F);
            model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
            model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
            model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
            return;
        }

        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }
}
