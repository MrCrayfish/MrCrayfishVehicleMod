package com.mrcrayfish.vehicle.proxy;

import com.google.common.base.Optional;
import com.mrcrayfish.vehicle.entity.EntityHelicopter;
import com.mrcrayfish.vehicle.entity.EntityPlane;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public interface Proxy
{
    default void preInit() {}

    default void init() {}

    default void postInit() {}

    default void playVehicleSound(EntityPlayer player, EntityPoweredVehicle vehicle) {}

    default void openVehicleEditWindow(int entityId, int windowId) {}

    default void syncStorageInventory(int entityId, NBTTagCompound tagCompound) {}

    default void syncHeldVehicle(int entityId, NBTTagCompound tagCompound) {}

    default void syncPlayerData(int entityId, int trailer, Optional<BlockPos> gasPumpPos) {}

    default void syncTrailer(int entityId, int trailer) {}

    default void syncGasPumpPos(int entityId, Optional<BlockPos> gasPumpPos) {}

    default void syncEntityFluid(int entityId, FluidStack stack) {}

    default void openStorageWindow(int entityId, int windowId) {}

    default void playSound(SoundEvent sound, BlockPos pos, float volume, float pitch) {}

    default EntityPoweredVehicle.AccelerationDirection getAccelerationDirection(EntityLivingBase entity)
    {
        return EntityPoweredVehicle.AccelerationDirection.NONE;
    }

    default EntityPoweredVehicle.TurnDirection getTurnDirection(EntityLivingBase entity)
    {
        return EntityPoweredVehicle.TurnDirection.FORWARD;
    }

    default float getTargetTurnAngle(EntityPoweredVehicle vehicle, boolean drifting)
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

    default EntityPlane.FlapDirection getFlapDirection()
    {
        return EntityPlane.FlapDirection.NONE;
    }

    default EntityHelicopter.AltitudeChange getAltitudeChange()
    {
        return EntityHelicopter.AltitudeChange.NONE;
    }

    default float getTravelDirection(EntityHelicopter vehicle)
    {
        return 0.0F;
    }

    default float getTravelSpeed(EntityHelicopter helicopter)
    {
        return 0.0F;
    }

    default float getPower(EntityPoweredVehicle vehicle)
    {
        return 1.0F;
    }

    default void syncPlayerSeat(int entityId, int seatIndex, UUID uuid) {}

    default void spawnWheelParticle(BlockPos pos, IBlockState state, double x, double y, double z, Vec3d motion) {}

}
