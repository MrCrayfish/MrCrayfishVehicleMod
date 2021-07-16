package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum EngineTier implements IEngineTier
{
    IRON(1.0F, 1F, 0F),
    GOLD(1.15F, 3F, 0F),
    DIAMOND(1.3F, 5F, 0F),
    NETHERITE(1.45F, 7F, 0F);

    private final float accelerationMultiplier;
    private final float additionalMaxSpeed;
    private final float fuelConsumption;

    EngineTier(float accelerationMultiplier, float additionalMaxSpeed, float fuelConsumption)
    {
        this.accelerationMultiplier = accelerationMultiplier;
        this.additionalMaxSpeed = additionalMaxSpeed;
        this.fuelConsumption = fuelConsumption;
    }

    @Override
    public float getAccelerationMultiplier()
    {
        return accelerationMultiplier;
    }

    @Override
    public float getAdditionalMaxSpeed()
    {
        return additionalMaxSpeed;
    }

    @Override
    public float getFuelConsumption()
    {
        return fuelConsumption;
    }
}
