package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum WheelType
{
    STANDARD(0.9F, 0.8F, 0.5F),
    SPORTS(1.0F, 0.75F, 0.5F),
    RACING(1.1F, 0.7F, 0.5F),
    OFF_ROAD(0.75F, 1.0F, 0.85F),
    SNOW(0.75F, 0.75F, 0.95F),
    ALL_TERRAIN(0.85F, 0.85F, 0.85F),
    PLASTIC(0.5F, 0.5F, 0.5F);

    float roadMultiplier;
    float dirtMultiplier;
    float snowMultiplier;

    WheelType(float roadMultiplier, float dirtMultiplier, float snowMultiplier)
    {
        this.roadMultiplier = roadMultiplier;
        this.dirtMultiplier = dirtMultiplier;
        this.snowMultiplier = snowMultiplier;
    }

    public float getRoadMultiplier()
    {
        return roadMultiplier;
    }

    public float getDirtMultiplier()
    {
        return dirtMultiplier;
    }

    public float getSnowMultiplier()
    {
        return snowMultiplier;
    }

    public void applyPhysics(EntityPoweredVehicle vehicle) {}

    public static WheelType getType(int index)
    {
        if(index < 0 || index >= values().length)
        {
            return STANDARD;
        }
        return WheelType.values()[index];
    }
}