package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractHelicopterRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SofacopterEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SofaHelicopterRenderer extends AbstractHelicopterRenderer<SofacopterEntity>
{
    public SofaHelicopterRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable SofacopterEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();
        this.renderDamagedPart(vehicle, SpecialModels.RED_SOFA.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.0, 8 * 0.0625, 0.0);
        this.renderDamagedPart(vehicle, SpecialModels.SOFA_HELICOPTER_ARM.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.0, 32 * 0.0625, 0.0);
        if(vehicle != null)
        {
            float bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(bladeRotation));
        }
        matrixStack.scale(1.5F, 1.5F, 1.5F);
        this.renderDamagedPart(vehicle, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

       /* GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.skid, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();*/
    }

    @Override
    public void applyPlayerModel(SofacopterEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightArm.xRot = (float) Math.toRadians(-55F);
        model.rightArm.yRot = (float) Math.toRadians(25F);
        model.leftArm.xRot = (float) Math.toRadians(-55F);
        model.leftArm.yRot = (float) Math.toRadians(-25F);
        model.rightLeg.xRot = (float) Math.toRadians(-90F);
        model.rightLeg.yRot = (float) Math.toRadians(15F);
        model.leftLeg.xRot = (float) Math.toRadians(-90F);
        model.leftLeg.yRot = (float) Math.toRadians(-15F);
    }

    @Override
    public void applyPlayerRender(SofacopterEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset() + 0.3) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            float entityYaw = entity.yRotO + (entity.yRot - entity.yRotO) * partialTicks;

            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-entityYaw));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks)));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(entityYaw));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.RED_SOFA, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SOFA_HELICOPTER_ARM, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 8 * 0.0625F, 0.0F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.SOFACOPTER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.SOFACOPTER.get(), parts, transforms);
        };
    }
}