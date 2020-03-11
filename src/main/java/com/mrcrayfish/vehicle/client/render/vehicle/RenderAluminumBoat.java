package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.AluminumBoatEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderAluminumBoat extends AbstractRenderVehicle<AluminumBoatEntity>
{
    private final RendererModel noWater;

    public RenderAluminumBoat()
    {
        this.noWater = (new RendererModel(new Model(), 0, 0)).setTextureSize(128, 64);
        this.noWater.addBox(-15F, -6F, -21F, 30, 8, 35, 0.0F);
    }

    @Override
    public void render(AluminumBoatEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.ALUMINUM_BOAT_BODY.getModel());
        //IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.waterMask());
        //noWater.render(matrixStack, buffer, light, OverlayTexture.DEFAULT_LIGHT);
    }

    @Override
    public void applyPlayerModel(AluminumBoatEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(20F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-20F);
    }

    @Override
    public void applyPlayerRender(AluminumBoatEntity entity, PlayerEntity player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).mul(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;

            GlStateManager.translated(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
            GlStateManager.rotatef(-8F * Math.min(1.0F, currentSpeedNormal), 1, 0, 0);
            GlStateManager.rotatef(turnAngleNormal * currentSpeedNormal * 15F, 0, 0, 1);
            GlStateManager.translated(-offsetX, -offsetY, -offsetZ);
        }
    }
}
