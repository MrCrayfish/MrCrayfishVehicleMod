package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.properties.MotorcycleProperties;
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
        float leanAngle = this.getMaxLeanAngle() * (this.getSteeringAngle() / this.getMaxSteeringAngle());
        leanAngle *= MathHelper.clamp(this.getSpeed() / 30.0, 0.0, 1.0);
        this.bodyRotationRoll = -leanAngle;
    }

    public float getMaxLeanAngle()
    {
        return this.getMotorcycleProperties().getMaxLeanAngle();
    }

    protected MotorcycleProperties getMotorcycleProperties()
    {
        return this.getProperties().getExtended(MotorcycleProperties.class);
    }
}
