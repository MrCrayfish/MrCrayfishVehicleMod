package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.render.AbstractRenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityMoped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

/**
 * Author: MrCrayfish
 */
public class RenderMoped extends AbstractRenderLandVehicle<EntityMoped>
{
    private static final ModelChest MOPED_CHEST = new ModelChest();
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    public final boolean isChristmas;

    public RenderMoped()
    {
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    @Override
    public void render(EntityMoped entity, float partialTicks)
    {
        Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(false);

        renderDamagedPart(entity, entity.body);

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -0.0625, 11.5 * 0.0625);
            GlStateManager.rotate(-22.5F, 1, 0, 0);

            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;

            GlStateManager.rotate(turnRotation / 2, 0, 1, 0);
            GlStateManager.rotate(22.5F, 1, 0, 0);
            GlStateManager.translate(0, 0, -11.5 * 0.0625);

            //Render handles bars
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.835, 0.525);
                GlStateManager.scale(0.8, 0.8, 0.8);
                renderDamagedPart(entity, entity.handleBar);
            }
            GlStateManager.popMatrix();

            //Render front bar and mud guard
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -0.12, 0.785);
                GlStateManager.rotate(-22.5F, 1, 0, 0);
                GlStateManager.scale(0.9, 0.9, 0.9);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.mudGuard, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            //Render front wheel
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, -0.4, 14.5 * 0.0625);
                float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                if(entity.isMoving())
                {
                    GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                }
                GlStateManager.scale(1.3F, 1.3F, 1.3F);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.wheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        if(entity.hasChest())
        {
            //Render chest
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, 0.3, -0.675);
                GlStateManager.rotate(180F, 0, 1, 0);
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                GlStateManager.scale(0.6F, 0.6F, 0.6F);
                GlStateManager.translate(-0.5F, -0.5F, -0.5F);

                if(this.isChristmas)
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_CHRISTMAS);
                }
                else
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_NORMAL);
                }
                MOPED_CHEST.renderAll();
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void applyPlayerModel(EntityMoped entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-75F - turnRotation);
        model.bipedRightArm.rotateAngleY = (float) Math.toRadians(7F);
        model.bipedRightArm.offsetZ -= 0.05 * wheelAngleNormal;
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-75F + turnRotation);
        model.bipedLeftArm.rotateAngleY = (float) Math.toRadians(-7F);
        model.bipedLeftArm.offsetZ -= 0.05 * -wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-55F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-55F);
    }

    @Override
    public void applyPlayerRender(EntityMoped entity, EntityPlayer player, float partialTicks)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        GlStateManager.translate(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
        GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
        GlStateManager.translate(0, -offset, 0);
    }
}
