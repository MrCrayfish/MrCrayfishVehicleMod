package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class GolfCartEntity extends LandVehicleEntity
{
    public GolfCartEntity(EntityType<? extends GolfCartEntity> type, World worldIn)
    {
        super(type, worldIn);
        //TODO figure out electric vehicles
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_MONO.get();
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_STEREO.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.6F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.4F;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
