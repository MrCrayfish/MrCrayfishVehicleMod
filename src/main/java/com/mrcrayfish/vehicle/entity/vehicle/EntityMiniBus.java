package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityMiniBus extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    public EntityMiniBus(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15F);
        this.setTurnSensitivity(2);
        this.setFuelCapacity(30000F);
        this.setFuelConsumption(0.375F);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.MINI_BUS_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.MINI_BUS_ENGINE_STEREO;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.75F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.25F;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.LARGE_MOTOR;
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
