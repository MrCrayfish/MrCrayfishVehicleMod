package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class PlayerModelHandler
{
    /**
     * Applies transformations to the player model when riding a vehicle and performing a wheelie
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onPreRender(PlayerModelEvent.Render.Pre event)
    {
        PlayerEntity player = event.getPlayer();
        Entity ridingEntity = player.getVehicle();
        if(ridingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) ridingEntity;
            this.applyPassengerTransformations(vehicle, player, event.getMatrixStack(), event.getBuilder(), event.getPartialTicks());
            this.applyWheelieTransformations(vehicle, player, event.getMatrixStack(), event.getPartialTicks());
        }
    }

    @SuppressWarnings("unchecked")
    private void applyPassengerTransformations(VehicleEntity vehicle, PlayerEntity player, MatrixStack matrixStack, IVertexBuilder builder, float partialTicks)
    {
        AbstractVehicleRenderer<VehicleEntity> render = (AbstractVehicleRenderer<VehicleEntity>) VehicleRenderRegistry.getRenderer((EntityType<? extends VehicleEntity>) vehicle.getType());
        if(render != null)
        {
            render.applyPlayerRender(vehicle, player, partialTicks, matrixStack, builder);
        }
    }

    /**
     * Applies transformations to the player model when the vehicle is performing a wheelie
     *
     * @param vehicle      the vehicle performing the wheelie
     * @param player       the player riding in the vehicle
     * @param matrixStack  the current matrix stack
     * @param partialTicks the current partial ticks
     */
    private void applyWheelieTransformations(VehicleEntity vehicle, PlayerEntity player, MatrixStack matrixStack, float partialTicks)
    {
        if(!(vehicle instanceof LandVehicleEntity))
            return;

        LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
        if(!landVehicle.canWheelie())
            return;

        int seatIndex = vehicle.getSeatTracker().getSeatIndex(player.getUUID());
        if(seatIndex == -1)
            return;

        VehicleProperties properties = landVehicle.getProperties();
        if(properties.getRearAxelVec() == null)
            return;

        Seat seat = properties.getSeats().get(seatIndex);
        Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
        double vehicleScale = properties.getBodyPosition().getScale();
        double playerScale = 32.0 / 30.0;
        double offsetX = -(seatVec.x * playerScale);
        double offsetY = (seatVec.y + player.getMyRidingOffset()) * playerScale + 24 * 0.0625 - properties.getWheelOffset() * 0.0625 * vehicleScale;
        double offsetZ = (seatVec.z * playerScale) - properties.getRearAxelVec().z * 0.0625 * vehicleScale;
        matrixStack.translate(offsetX, offsetY, offsetZ);
        float wheelieProgress = MathHelper.lerp(partialTicks, landVehicle.prevWheelieCount, landVehicle.wheelieCount) / 4F;
        wheelieProgress = (float) (1.0 - Math.pow(1.0 - wheelieProgress, 2));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F * wheelieProgress));
        matrixStack.translate(-offsetX, -offsetY, -offsetZ);
    }

    @SubscribeEvent
    public void onSetupAngles(PlayerModelEvent.SetupAngles.Post event)
    {
        PlayerEntity player = event.getPlayer();

        if(player.equals(Minecraft.getInstance().player) && Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON)
            return;

        if(SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).isPresent())
        {
            FuelingHandler.applyFuelingPose(player, event.getModelPlayer());
            return;
        }

        SprayCanHandler.applySprayCanPose(player, event.getModelPlayer());
        this.applyPassengerPose(player, event.getModelPlayer(), event.getPartialTicks());
    }

    /**
     * Applies a pose to the player model when the player is riding a vehicle. The pose varies
     * depending on the vehicle they are riding.
     *
     * @param player the player riding the vehicle
     * @param model the model of the player
     * @param partialTicks the current partial ticks
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applyPassengerPose(PlayerEntity player, PlayerModel model, float partialTicks)
    {
        Entity ridingEntity = player.getVehicle();
        if(!(ridingEntity instanceof VehicleEntity))
            return;

        VehicleEntity vehicle = (VehicleEntity) ridingEntity;
        AbstractVehicleRenderer<VehicleEntity> render = (AbstractVehicleRenderer<VehicleEntity>) VehicleRenderRegistry.getRenderer((EntityType<? extends VehicleEntity>) vehicle.getType());
        if(render != null)
        {
            render.applyPlayerModel(vehicle, player, model, partialTicks);
        }
    }
}
