package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.EntityDirtBike;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderDirtBike extends AbstractRenderVehicle<EntityDirtBike>
{
    @Override
    public void render(EntityDirtBike entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_BODY.getModel());

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.0, 10.5 * 0.0625);
        GlStateManager.rotate(-22.5F, 1, 0, 0);

        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 25F;

        GlStateManager.rotate(turnRotation, 0, 1, 0);
        GlStateManager.rotate(22.5F, 1, 0, 0);
        GlStateManager.translate(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(entity, SpecialModels.DIRT_BIKE_HANDLES.getModel());

        if(entity.hasWheels())
        {
            Wheel wheel = entity.getProperties().getWheels().stream().filter(wheel1 -> wheel1.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
            if(wheel != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, -0.5, 0);
                GlStateManager.translate(wheel.getOffsetX() * 0.0625, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
                float frontWheelSpin = entity.prevFrontWheelRotation + (entity.frontWheelRotation - entity.prevFrontWheelRotation) * partialTicks;
                if(entity.isMoving())
                {
                    GlStateManager.rotate(-frontWheelSpin, 1, 0, 0);
                }
                GlStateManager.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                GlStateManager.rotate(180F, 0, 1, 0);
                this.renderDamagedPart(entity, RenderUtil.getWheelModel(entity));
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(EntityDirtBike entity, EntityPlayer player, ModelPlayer model, float partialTicks)
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
    public void applyPlayerRender(EntityDirtBike entity, EntityPlayer player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = -seatVec.z * scale;
            GlStateManager.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            GlStateManager.rotate(turnAngleNormal * currentSpeedNormal * 20F, 0, 0, 1);
            GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
        }
    }
}