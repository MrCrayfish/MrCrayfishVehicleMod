package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class SportsPlaneEntity extends PlaneEntity
{
    public SportsPlaneEntity(EntityType<? extends SportsPlaneEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSteeringAngle(25);
        this.setFuelCapacity(75000F);
        this.setFuelConsumption(1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling()
    {
        return this.getBoundingBox().inflate(1.5);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_SPORTS_PLANE_ENGINE.get();
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
