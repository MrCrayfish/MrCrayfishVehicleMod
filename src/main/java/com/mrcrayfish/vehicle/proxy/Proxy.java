package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

/**
 * Author: MrCrayfish
 */
public interface Proxy
{
    default void setupClient() {}

    default void playVehicleSound(PlayerEntity player, PoweredVehicleEntity vehicle) {}

    default void openVehicleEditWindow(int entityId, int windowId) {}

    default void syncStorageInventory(int entityId, CompoundNBT compound) {}

    default void syncEntityFluid(int entityId, FluidStack stack) {}

    default void openStorageWindow(int entityId, int windowId) {}

    default void playSound(SoundEvent sound, BlockPos pos, float volume, float pitch) {}

    default void playSound(SoundEvent sound, float volume, float pitch) {}

    default PoweredVehicleEntity.AccelerationDirection getAccelerationDirection(LivingEntity entity)
    {
        return PoweredVehicleEntity.AccelerationDirection.NONE;
    }

    default PoweredVehicleEntity.TurnDirection getTurnDirection(LivingEntity entity)
    {
        return PoweredVehicleEntity.TurnDirection.FORWARD;
    }

    default float getTargetTurnAngle(PoweredVehicleEntity vehicle, boolean drifting)
    {
        return 0F;
    }

    default boolean isDrifting()
    {
        return false;
    }

    default boolean isHonking()
    {
        return false;
    }

    default PlaneEntity.FlapDirection getFlapDirection()
    {
        return PlaneEntity.FlapDirection.NONE;
    }

    default HelicopterEntity.AltitudeChange getAltitudeChange()
    {
        return HelicopterEntity.AltitudeChange.NONE;
    }

    default float getTravelDirection(HelicopterEntity vehicle)
    {
        return 0.0F;
    }

    default float getTravelSpeed(HelicopterEntity helicopter)
    {
        return 0.0F;
    }

    default float getPower(PoweredVehicleEntity vehicle)
    {
        return 1.0F;
    }

    default boolean canApplyVehicleYaw(Entity passenger)
    {
        return false;
    }
}
