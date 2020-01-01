package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class DuneBuggyEntity extends LandVehicleEntity implements IEntityRaytraceable
{
    public DuneBuggyEntity(World worldIn)
    {
        super(ModEntities.DUNE_BUGGY, worldIn);
        this.setMaxSpeed(10);
        this.stepHeight = 0.5F;
        this.setFuelCapacity(5000F);
    }

    @Override
    public void registerData()
    {
        super.registerData();
        this.dataManager.set(WHEEL_TYPE, WheelType.PLASTIC.ordinal());
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3.25 * 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.ELECTRIC_MOTOR;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
