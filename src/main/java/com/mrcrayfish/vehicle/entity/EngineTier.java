package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum EngineTier //TODO abstract
{
    IRON(1.0F, 1F, 0F),
    GOLD(1.15F, 3F, 0F),
    DIAMOND(1.3F, 5F, 0F),
    NETHERITE(1.45F, 7F, 0F);

    float accelerationMultiplier;
    float additionalMaxSpeed;
    float fuelConsumption;

    EngineTier(float accelerationMultiplier, float additionalMaxSpeed, float fuelConsumption)
    {
        this.accelerationMultiplier = accelerationMultiplier;
        this.additionalMaxSpeed = additionalMaxSpeed;
        this.fuelConsumption = fuelConsumption;
    }

    public float getAccelerationMultiplier()
    {
        return accelerationMultiplier;
    }

    public float getAdditionalMaxSpeed()
    {
        return additionalMaxSpeed;
    }

    public float getFuelConsumption()
    {
        return fuelConsumption;
    }

    public static EngineTier getType(int index)
    {
        if(index < 0 || index >= values().length)
        {
            return IRON;
        }
        return EngineTier.values()[index];
    }
}
