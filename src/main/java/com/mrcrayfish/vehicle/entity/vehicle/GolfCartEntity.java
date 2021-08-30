package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class GolfCartEntity extends HelicopterEntity
{
    public GolfCartEntity(EntityType<? extends GolfCartEntity> type, World worldIn)
    {
        super(type, worldIn);
        //TODO figure out electric vehicles
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_VEHICLE_HELICOPTER_ROTOR.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.0F;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
