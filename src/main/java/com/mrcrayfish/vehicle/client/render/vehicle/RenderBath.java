package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntityBath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderBath extends AbstractRenderVehicle<EntityBath>
{
    @Override
    public void render(EntityBath entity, float partialTicks)
    {
        GlStateManager.rotate(90F, 0, 1, 0);
        renderDamagedPart(entity, entity.body);
    }

    @Override
    public void applyPlayerRender(EntityBath entity, EntityPlayer player, float partialTicks)
    {
        double offsetY = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset() - 0.5;//TODO make this last variable a variable in entity plane
        GlStateManager.translate(0, offsetY, 0);
        float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
        float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
        GlStateManager.rotate(-bodyPitch, 1, 0, 0);
        GlStateManager.rotate(bodyRoll, 0, 0, 1);
        GlStateManager.translate(0, -offsetY, 0);
    }

    @Override
    public void applyPlayerModel(EntityBath entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedHead.isHidden = true;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(5F);
        model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(0F);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-80F);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-5F);
        model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(0F);
    }
}
