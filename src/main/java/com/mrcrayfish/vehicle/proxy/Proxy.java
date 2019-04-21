package com.mrcrayfish.vehicle.proxy;

import com.mrcrayfish.vehicle.entity.EntityHelicopter;
import com.mrcrayfish.vehicle.entity.EntityPlane;
import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

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
}
