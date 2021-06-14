package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity.FuelPortType;

/**
 * Author: MrCrayfish
 */
public class ATVEntity extends LandVehicleEntity
{
    public ATVEntity(EntityType<? extends ATVEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(15);
        this.setFuelCapacity(20000F);
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ATV_ENGINE_MONO.get();
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ATV_ENGINE_STEREO.get();
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }
}
