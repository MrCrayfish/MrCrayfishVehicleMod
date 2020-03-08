package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.entity.vehicle.EntityMiniBike;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: MrCrayfish
 */
public class RenderMiniBike extends AbstractRenderVehicle<EntityMiniBike>
{
    @Override
    public void render(EntityMiniBike entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.MINI_BIKE_BODY.getModel());

        //Render the handles bars
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, 0, 10.5 * 0.0625);
            GlStateManager.rotate(-22.5F, 1, 0, 0);

            float wheelScale = 1.65F;
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;

            GlStateManager.rotate(turnRotation, 0, 1, 0);
            GlStateManager.rotate(22.5F, 1, 0, 0);
            GlStateManager.translate(0, 0, -10.5 * 0.0625);

            this.renderDamagedPart(entity, SpecialModels.MINI_BIKE_HANDLE_BAR.getModel());

            if(entity.hasWheels())
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0, -0.5 + 1.7 * 0.0625, 13 * 0.0625);
                    float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                    if(entity.isMoving())
                    {
                        GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                    }
                    GlStateManager.scale(wheelScale, wheelScale, wheelScale);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    IBakedModel model = RenderUtil.getWheelModel(entity);
                    if(model != null)
                    {
                        RenderUtil.renderColoredModel(model, ItemCameraTransforms.TransformType.NONE, entity.getWheelColor());
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityMiniBike entity, EntityPlayer player, ModelPlayer model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 8F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        model.bipedRightArm.offsetZ = -0.1F * wheelAngleNormal;
        model.bipedLeftArm.offsetZ = 0.1F * wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(EntityMiniBike entity, EntityPlayer player, float partialTicks)
    {
        double offset = 24 * 0.0625 + entity.getMountedYOffset() + player.getYOffset();
        GlStateManager.translate(0, offset, 0);
        float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
        float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
        GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
        GlStateManager.translate(0, -offset, 0);
    }
}
