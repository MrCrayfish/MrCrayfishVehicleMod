package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySportsPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderSportsPlane extends AbstractRenderVehicle<EntitySportsPlane>
{
    public RenderSportsPlane()
    {
        this.setFuelPortPosition(EntitySportsPlane.FUEL_PORT_POSITION);
    }

    @Override
    public void render(EntitySportsPlane entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -3 * 0.0625, 8 * 0.0625);
            GlStateManager.translate(8 * 0.0625, 0, 0);
            GlStateManager.translate(6 * 0.0625, 0, 0);
            GlStateManager.rotate(-5F, 1, 0, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(entity.wing, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -3 * 0.0625, 8 * 0.0625);
            GlStateManager.rotate(180F, 0, 0, 1);
            GlStateManager.translate(8 * 0.0625, 0.0625, 0);
            GlStateManager.translate(6 * 0.0625, 0, 0);
            GlStateManager.rotate(5F, 1, 0, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(entity.wing, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -0.5, 0);
            GlStateManager.scale(0.85, 0.85, 0.85);
            renderWheel(entity, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks);
            renderWheel(entity, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks);
            renderWheel(entity, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            float propellerRotation = entity.prevPropellerRotation + (entity.propellerRotation - entity.prevPropellerRotation) * partialTicks;
            GlStateManager.translate(0, -1.5 * 0.0625, 22.2 * 0.0625);
            GlStateManager.rotate(propellerRotation, 0, 0, 1);
            Minecraft.getMinecraft().getRenderItem().renderItem(entity.propeller, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    private void renderWheel(EntitySportsPlane vehicle, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(offsetX, offsetY, offsetZ);
            Minecraft.getMinecraft().getRenderItem().renderItem(vehicle.wheelCover, ItemCameraTransforms.TransformType.NONE);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -2.25F / 16F, 0);
                GlStateManager.pushMatrix();
                {
                    if(vehicle.isMoving())
                    {
                        float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
                        GlStateManager.rotate(-wheelRotation, 1, 0, 0);
                    }
                    GlStateManager.scale(0.8F, 0.8F, 0.8F);
                    Minecraft.getMinecraft().getRenderItem().renderItem(vehicle.wheel, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            GlStateManager.rotate(legRotation, 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(vehicle.leg, ItemCameraTransforms.TransformType.NONE);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntitySportsPlane entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
    }

    @Override
    public void applyPlayerRender(EntitySportsPlane entity, EntityPlayer player, float partialTicks)
    {
        GlStateManager.translate(0, -8 * 0.0625, 0.5);
        GlStateManager.translate(0, 0.625, 0);
        float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
        float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
        GlStateManager.rotate(bodyRoll, 0, 0, 1);
        GlStateManager.rotate(-bodyPitch, 1, 0, 0);
        GlStateManager.translate(0, -0.625, 0);
        GlStateManager.translate(0, 8 * 0.0625, -0.5);
    }
}
