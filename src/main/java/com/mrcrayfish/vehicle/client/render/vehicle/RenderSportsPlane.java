package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MrCrayfish
 */
public class RenderSportsPlane extends AbstractRenderVehicle<SportsPlaneEntity>
{
    @Override
    public void render(SportsPlaneEntity entity, float partialTicks)
    {
        this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE.getModel());

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(0, -3 * 0.0625, 8 * 0.0625);
            GlStateManager.translated(8 * 0.0625, 0, 0);
            GlStateManager.translated(6 * 0.0625, 0, 0);
            GlStateManager.rotatef(-5F, 1, 0, 0);
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_WING.getModel());
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(0, -3 * 0.0625, 8 * 0.0625);
            GlStateManager.rotatef(180F, 0, 0, 1);
            GlStateManager.translated(8 * 0.0625, 0.0625, 0);
            GlStateManager.translated(6 * 0.0625, 0, 0);
            GlStateManager.rotatef(5F, 1, 0, 0);
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_WING.getModel());
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(0, -0.5, 0);
            GlStateManager.scalef(0.85F, 0.85F, 0.85F);
            this.renderWheel(entity, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks);
            this.renderWheel(entity, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks);
            this.renderWheel(entity, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            float propellerRotation = entity.prevPropellerRotation + (entity.propellerRotation - entity.prevPropellerRotation) * partialTicks;
            GlStateManager.translated(0, -1.5 * 0.0625, 22.2 * 0.0625);
            GlStateManager.rotatef(propellerRotation, 0, 0, 1);
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_PROPELLER.getModel());
        }
        GlStateManager.popMatrix();
    }

    private void renderWheel(SportsPlaneEntity vehicle, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(offsetX, offsetY, offsetZ);
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WHEEL_COVER.getModel());

            GlStateManager.pushMatrix();
            {
                GlStateManager.translated(0, -2.25F / 16F, 0);
                GlStateManager.pushMatrix();
                {
                    if(vehicle.isMoving())
                    {
                        float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
                        GlStateManager.rotatef(-wheelRotation, 1, 0, 0);
                    }
                    GlStateManager.scalef(0.8F, 0.8F, 0.8F);
                    RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL.get())), ItemCameraTransforms.TransformType.NONE, false, -1);
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            GlStateManager.rotatef(legRotation, 0, 1, 0);
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_LEG.getModel());
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void applyPlayerModel(SportsPlaneEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.bipedRightLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedRightLeg.rotateAngleY = (float) Math.toRadians(10F);
        model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(-85F);
        model.bipedLeftLeg.rotateAngleY = (float) Math.toRadians(-10F);
    }

    @Override
    public void applyPlayerRender(SportsPlaneEntity entity, PlayerEntity player, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset() - 0.5) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            GlStateManager.translated(offsetX, offsetY, offsetZ);
            float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
            float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
            GlStateManager.rotatef(bodyRoll, 0, 0, 1);
            GlStateManager.rotatef(-bodyPitch, 1, 0, 0);
            GlStateManager.translated(-offsetX, -offsetY, -offsetX);
        }
    }
}
