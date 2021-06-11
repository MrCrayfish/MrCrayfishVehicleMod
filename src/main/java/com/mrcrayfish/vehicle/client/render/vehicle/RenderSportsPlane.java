package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderVehicle;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Author: MrCrayfish
 */
public class RenderSportsPlane extends AbstractRenderVehicle<SportsPlaneEntity>
{
    @Override
    public void render(SportsPlaneEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.push();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.translate(8 * 0.0625, 0, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-5F));
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.pop();

        matrixStack.push();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.rotate(Axis.POSITIVE_Z.rotationDegrees(180F));
            matrixStack.translate(8 * 0.0625, 0.0625, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(5F));
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.pop();

        matrixStack.push();
        {
            matrixStack.translate(0, -0.5, 0);
            matrixStack.scale(0.85F, 0.85F, 0.85F);
            renderWheel(entity, matrixStack, renderTypeBuffer, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks, light);
            renderWheel(entity, matrixStack, renderTypeBuffer, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks, light);
            renderWheel(entity, matrixStack, renderTypeBuffer, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks, light);
        }
        matrixStack.pop();

        matrixStack.push();
        {
            float propellerRotation = entity.prevPropellerRotation + (entity.propellerRotation - entity.prevPropellerRotation) * partialTicks;
            matrixStack.translate(0, -1.5 * 0.0625, 22.2 * 0.0625);
            matrixStack.rotate(Axis.POSITIVE_Z.rotationDegrees(propellerRotation));
            this.renderDamagedPart(entity, SpecialModels.SPORTS_PLANE_PROPELLER.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.pop();
    }

    private void renderWheel(SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks, int light)
    {
        matrixStack.push();
        {
            matrixStack.translate(offsetX, offsetY, offsetZ);
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WHEEL_COVER.getModel(), matrixStack, renderTypeBuffer, light);

            matrixStack.push();
            {
                matrixStack.translate(0, -2.25F / 16F, 0);
                matrixStack.push();
                {
                    if(vehicle.isMoving())
                    {
                        float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
                        matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-wheelRotation));
                    }
                    matrixStack.scale(0.8F, 0.8F, 0.8F);
                    RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL.get())), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
                }
                matrixStack.pop();
            }
            matrixStack.pop();

            matrixStack.rotate(Axis.POSITIVE_Y.rotationDegrees(legRotation));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_LEG.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.pop();
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
    public void applyPlayerRender(SportsPlaneEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUniqueID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getYOffset() - 0.5) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
            float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
            matrixStack.rotate(Axis.POSITIVE_Z.rotationDegrees(bodyRoll));
            matrixStack.rotate(Axis.POSITIVE_X.rotationDegrees(-bodyPitch));
            matrixStack.translate(-offsetX, -offsetY, -offsetX);
        }
    }
}
