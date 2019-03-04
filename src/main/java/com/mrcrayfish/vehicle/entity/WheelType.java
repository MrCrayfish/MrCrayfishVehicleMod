package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum WheelType
{
    STANDARD("stock", 0.9F, 0.8F, 0.5F),
    SPORTS("sports", 1.0F, 0.75F, 0.5F),
    RACING("racing", 1.1F, 0.7F, 0.5F),
    OFF_ROAD("off_road", 0.75F, 1.0F, 0.85F),
    SNOW("snow", 0.75F, 0.75F, 0.95F),
    ALL_TERRAIN("all_terrain", 0.85F, 0.85F, 0.85F),
    PLASTIC("plastic", 0.5F, 0.5F, 0.5F);

    String id;
    float roadMultiplier;
    float dirtMultiplier;
    float snowMultiplier;

    WheelType(String id, float roadMultiplier, float dirtMultiplier, float snowMultiplier)
    {
        this.id = id;
        this.roadMultiplier = roadMultiplier;
        this.dirtMultiplier = dirtMultiplier;
        this.snowMultiplier = snowMultiplier;
    }

    public String getId()
    {
        return id;
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