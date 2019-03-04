package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityLawnMower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderLawnMower extends AbstractRenderLandVehicle<EntityLawnMower>
{
    @Override
    public void render(EntityLawnMower entity, float partialTicks)
    {
        //Body
        renderDamagedPart(entity, entity.body);

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0.4, -0.15);
            GlStateManager.rotate(-45F, 1, 0, 0);
            GlStateManager.scale(0.9, 0.9, 0.9);

            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            GlStateManager.rotate(turnRotation, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityLawnMower entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(7F);
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
    }
}
