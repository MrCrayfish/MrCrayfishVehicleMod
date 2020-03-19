package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.EntitySofacopter;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderCouchHelicopter extends AbstractRenderVehicle<EntitySofacopter>
{
    @Override
    public void render(EntitySofacopter entity, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90F, 0, 1, 0);
        this.renderDamagedPart(entity, SpecialModels.COUCH.getModel());
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 8 * 0.0625, 0);
        this.renderDamagedPart(entity, SpecialModels.COUCH_HELICOPTER_ARM.getModel());
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 32 * 0.0625, 0);
        float bladeRotation = entity.prevBladeRotation + (entity.bladeRotation - entity.prevBladeRotation) * partialTicks;
        GlStateManager.rotate(bladeRotation, 0, 1, 0);
        GlStateManager.scale(1.5, 1.5, 1.5);
        this.renderDamagedPart(entity, SpecialModels.BLADE.getModel());
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
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale());
            seatVec = new Vec3d(-seatVec.x, seatVec.y, seatVec.z);
            seatVec = seatVec.scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset() + 0.3) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

            GlStateManager.translate(offsetX, offsetY, offsetZ);
            GlStateManager.rotate(-entityYaw, 0, 1, 0);
            GlStateManager.rotate(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks), 0, 0, 1);
            GlStateManager.rotate(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks, 1, 0, 0);
            GlStateManager.rotate(entityYaw, 0, 1, 0);
            GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
        }
    }
}