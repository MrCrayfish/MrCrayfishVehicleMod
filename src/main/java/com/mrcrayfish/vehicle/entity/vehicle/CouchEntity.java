package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class CouchEntity extends LandVehicleEntity
{
    public CouchEntity(EntityType<? extends CouchEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(10);
        this.entityData.set(COLOR, 11546150);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_ATV_ENGINE.get();
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
