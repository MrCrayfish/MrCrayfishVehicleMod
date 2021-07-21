package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum EngineTier implements IEngineTier
{
    IRON(1.0F, 0F),
    GOLD(1.1F, 2F),
    DIAMOND(1.2F, 5F),
    NETHERITE(1.4F, 5F);

    private final float accelerationMultiplier;
    private final float additionalMaxSpeed;

    EngineTier(float accelerationMultiplier, float additionalMaxSpeed)
    {
        this.accelerationMultiplier = accelerationMultiplier;
        this.additionalMaxSpeed = additionalMaxSpeed;
    }

    @Override
    public float getAccelerationMultiplier()
    {
        return this.accelerationMultiplier;
    }

    @Override
    public float getAdditionalMaxSpeed()
    {
        return this.additionalMaxSpeed;
    }
}
