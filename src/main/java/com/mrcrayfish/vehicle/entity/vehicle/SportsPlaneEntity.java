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
        //this.setMaxSpeed(25F);
        this.setMaxSteeringAngle(25);
        this.setSteeringSpeed(2);
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
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    @Override
    public float getFlapSensitivity()
    {
        return 0.1F;
    }

    @Override
    public float getElevatorSensitivity()
    {
        return 0.075F;
    }

    @Override
    public float getMaxTurnAngle()
    {
        return 2F;
    }
}
