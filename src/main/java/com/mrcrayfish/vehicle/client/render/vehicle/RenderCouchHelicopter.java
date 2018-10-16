package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySofacopter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderCouchHelicopter extends AbstractRenderVehicle<EntitySofacopter>
{
    public RenderCouchHelicopter()
    {
        this.setFuelPortPosition(EntitySofacopter.FUEL_PORT_POSITION);
    }

    @Override
    public void render(EntitySofacopter entity, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90F, 0, 1, 0);
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 8 * 0.0625, 0);
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.arm, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 32 * 0.0625, 0);
        float bladeRotation = entity.prevBladeRotation + (entity.bladeRotation - entity.prevBladeRotation) * partialTicks;
        GlStateManager.rotate(bladeRotation, 0, 1, 0);
        GlStateManager.scale(1.5, 1.5, 1.5);
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.blade, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();

       /* GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.skid, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();*/
    }

    @Override
    public void applyPlayerModel(EntitySofacopter entity, EntityPlayer player, ModelPlayer model, float partialTicks)
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

    @Override
    public void applyPlayerRender(EntitySofacopter entity, EntityPlayer player, float partialTicks)
    {
        float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float playerOffset = (float) ((entity.getMountedYOffset() + player.getYOffset()) * 16.0F - 14.0F * 0.0625F);
        GlStateManager.translate(0, -playerOffset, 0);
        GlStateManager.rotate(-entityYaw, 0, 1, 0);
        GlStateManager.rotate(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks), 0, 0, 1);
        GlStateManager.rotate(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks, 1, 0, 0);
        GlStateManager.rotate(entityYaw, 0, 1, 0);
        GlStateManager.translate(0, playerOffset, 0);
    }
}