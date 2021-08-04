package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum WheelType implements IWheelType
{
    STANDARD(1.1F, 1.2F, 2.0F),
    SPORTS(1.0F, 1.4F, 2.0F),
    RACING(0.9F, 1.5F, 2.0F),
    OFF_ROAD(1.2F, 0.9F, 1.2F),
    SNOW(1.8F, 1.0F, 0.7F),
    ALL_TERRAIN(1.1F, 1.1F, 1.1F),
    PLASTIC(2.0F, 2.0F, 2.0F);

    private final float roadFrictionFactor;
    private final float dirtFrictionFactor;
    private final float snowFrictionFactor;

    WheelType(float roadFrictionFactor, float dirtFrictionFactor, float snowFrictionFactor)
    {
        this.roadFrictionFactor = roadFrictionFactor;
        this.dirtFrictionFactor = dirtFrictionFactor;
        this.snowFrictionFactor = snowFrictionFactor;
    }

    @Override
    public float getRoadFrictionFactor()
    {
        return this.roadFrictionFactor;
    }

    @Override
    public float getDirtFrictionFactor()
    {
        return this.dirtFrictionFactor;
    }

    @Override
    public float getSnowFrictionFactor()
    {
        return this.snowFrictionFactor;
    }
}