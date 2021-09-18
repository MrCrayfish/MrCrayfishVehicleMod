package com.mrcrayfish.vehicle.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class MotorcycleEntity extends LandVehicleEntity
{
    public MotorcycleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    protected void updateBodyRotations()
    {
        super.updateBodyRotations();
        float leanAngle = 45F * (this.getSteeringAngle() / this.getMaxSteeringAngle());
        leanAngle *= MathHelper.clamp(this.getSpeed() / 30.0, 0.0, 1.0);
        this.bodyRotationRoll = -leanAngle;
    }
}
