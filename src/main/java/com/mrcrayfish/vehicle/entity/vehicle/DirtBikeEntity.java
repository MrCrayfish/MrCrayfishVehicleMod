package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class DirtBikeEntity extends MotorcycleEntity
{
    public DirtBikeEntity(EntityType<? extends DirtBikeEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(18F);
        this.setTurnSensitivity(10);
        this.setFuelCapacity(20000F);
        this.setFuelConsumption(0.35F);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.DIRT_BIKE_ENGINE_MONO.get();
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.DIRT_BIKE_ENGINE_STEREO.get();
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.85F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.5F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(-0.0625, 1.25, -1);
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
