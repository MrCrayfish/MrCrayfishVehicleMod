package com.mrcrayfish.vehicle.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class MotorcycleEntity extends LandVehicleEntity
{
    public float prevLeanAngle;
    public float leanAngle;

    public MotorcycleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void tick()
    {
        this.prevLeanAngle = this.leanAngle;
        super.tick();
        this.leanAngle = this.turnAngle / (float) getMaxTurnAngle();
    }
}
