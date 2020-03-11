package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SofacopterEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderCouchHelicopter extends AbstractRenderVehicle<SofacopterEntity>
{
    @Override
    public void render(SofacopterEntity entity, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.renderDamagedPart(entity, SpecialModels.RED_SOFA.getModel());
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(0.0, 8 * 0.0625, 0.0);
        this.renderDamagedPart(entity, SpecialModels.SOFA_HELICOPTER_ARM.getModel());
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(0.0, 32 * 0.0625, 0.0);
        float bladeRotation = entity.prevBladeRotation + (entity.bladeRotation - entity.prevBladeRotation) * partialTicks;
        GlStateManager.rotatef(bladeRotation, 0, 1, 0);
        GlStateManager.scalef(1.5F, 1.5F, 1.5F);
        this.renderDamagedPart(entity, SpecialModels.ALUMINUM_BOAT_BODY.getModel());
        GlStateManager.popMatrix();

       /* GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.skid, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();*/
    }

    @Override
    public void applyPlayerModel(SofacopterEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
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
    public void applyPlayerRender(SofacopterEntity entity, PlayerEntity player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).mul(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset() + 0.3) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

            GlStateManager.translated(offsetX, offsetY, offsetZ);
            GlStateManager.rotatef(-entityYaw, 0, 1, 0);
            GlStateManager.rotatef(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks), 0, 0, 1);
            GlStateManager.rotatef(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks, 1, 0, 0);
            GlStateManager.rotatef(entityYaw, 0, 1, 0);
            GlStateManager.translated(-offsetX, -offsetY, -offsetZ);
        }
    }
}