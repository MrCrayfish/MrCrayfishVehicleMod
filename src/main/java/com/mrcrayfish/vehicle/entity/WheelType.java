package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum WheelType
{
    NONE(1.0F, 0.75F, 0.25F),
    STANDARD(1.0F, 0.8F, 0.25F),
    SPORTS(1.1F, 0.7F, 0.25F),
    RACING(1.25F, 0.5F, 0.15F),
    DIRT(0.9F, 1.0F, 0.5F),
    OFF_ROAD(0.8F, 1.1F, 0.50F),
    SNOW(0.75F, 0.9F, 1.0F),
    ALL_TERRAIN(0.85F, 0.85F, 0.85F),
    WATER(0.25F, 0.25F, 0.25F)
    {
        @Override
        public void applyPhysics(EntityPoweredVehicle vehicle)
        {
            if(vehicle.isInWater())
            {
                vehicle.motionY += 0.1;
            }
        }
    };

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
            return NONE;
        }
        return WheelType.values()[index];
    }
}