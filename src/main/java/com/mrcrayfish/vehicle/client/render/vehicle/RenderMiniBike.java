package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBikeEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderMiniBike extends AbstractRenderVehicle<MiniBikeEntity>
{
    @Override
    public void render(MiniBikeEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.MINI_BIKE_BODY.getModel());

        //Render the handles bars
        GlStateManager.pushMatrix();

        GlStateManager.translated(0.0, 0.0, 10.5 * 0.0625);
        GlStateManager.rotatef(-22.5F, 1, 0, 0);

        float wheelScale = 1.65F;
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        GlStateManager.rotatef(turnRotation, 0, 1, 0);
        GlStateManager.rotatef(22.5F, 1, 0, 0);
        GlStateManager.translated(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(entity, SpecialModels.MINI_BIKE_HANDLES.getModel());

        if(entity.hasWheels())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(0, -0.5 + 1.7 * 0.0625, 13 * 0.0625);
            float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
            if(entity.isMoving())
            {
                GlStateManager.rotatef(-frontWheelSpin, 1, 0, 0);
            }
            GlStateManager.scalef(wheelScale, wheelScale, wheelScale);
            GlStateManager.rotatef(180F, 0, 1, 0);
            RenderUtil.renderColoredModel(RenderUtil.getModel(ItemLookup.getWheel(entity)), ItemCameraTransforms.TransformType.NONE, false, -1);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(MiniBikeEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 8F;
        model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
        model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        //model.bipedRightArm.offsetZ = -0.1F * wheelAngleNormal;
        //model.bipedLeftArm.offsetZ = 0.1F * wheelAngleNormal;
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-65F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(MiniBikeEntity entity, PlayerEntity player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = -seatVec.z * scale;
            GlStateManager.translated(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotatef(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translated(-offsetX, -offsetY, -offsetZ);
        }
    }
}
