package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.DirtBikeEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderDirtBike extends AbstractRenderVehicle<DirtBikeEntity>
{
    @Override
    public void render(DirtBikeEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_BODY.getModel());

        GlStateManager.pushMatrix();
        GlStateManager.translated(0.0, 0.0, 10.5 * 0.0625);
        GlStateManager.rotatef(-22.5F, 1, 0, 0);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        GlStateManager.rotatef(turnRotation, 0, 1, 0);
        GlStateManager.rotatef(22.5F, 1, 0, 0);
        GlStateManager.translated(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_HANDLES.getModel());

        if(entity.hasWheels())
        {
            Wheel wheel = entity.getProperties().getWheels().stream().filter(wheel1 -> wheel1.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
            if(wheel != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translated(0, -0.5, 0);
                GlStateManager.translated(wheel.getOffsetX() * 0.0625, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
                float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                if(entity.isMoving())
                {
                    GlStateManager.rotatef(-frontWheelSpin, 1, 0, 0);
                }
                GlStateManager.scalef(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                GlStateManager.rotatef(180F, 0, 1, 0);
                this.renderDamagedPart(entity, RenderUtil.getModel(ItemLookup.getWheel(entity)));
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(DirtBikeEntity entity, PlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index == 0)
        {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 8F;
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-55F - turnRotation);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-55F + turnRotation);
        }
        else if(index == 1)
        {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-45F);
            model.bipedRightArm.rotateAngleZ = (float) Math.toRadians(-10F);
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-45F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(10F);
        }

        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-45F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(30F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-45F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(DirtBikeEntity entity, PlayerEntity player, float partialTicks)
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