package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractBoatRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.AluminumBoatEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class AluminumBoatRenderer extends AbstractBoatRenderer<AluminumBoatEntity>
{
    private final ModelRenderer noWater;

    public AluminumBoatRenderer(VehicleProperties properties)
    {
        super(properties);
        this.noWater = (new ModelRenderer(new Model(resource -> RenderType.waterMask()){
            @Override
            public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {}
        }, 0, 0)).setTexSize(128, 64);
        this.noWater.addBox(-15F, -6F, -21F, 30, 8, 35, 0.0F);
    }

    @Override
    protected void render(@Nullable AluminumBoatEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.waterMask());
        this.noWater.render(matrixStack, buffer, light, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void applyPlayerModel(AluminumBoatEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(20F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-20F);
    }

    @Override
    public void applyPlayerRender(AluminumBoatEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;

            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-8F * Math.min(1.0F, currentSpeedNormal)));
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(turnAngleNormal * currentSpeedNormal * 15F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.ALUMINUM_BOAT_BODY, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.ALUMINUM_BOAT.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
