package com.mrcrayfish.vehicle.entity;

/**
 * Author: MrCrayfish
 */
public enum WheelType implements IWheelType
{
    STANDARD(1.1F, 1.3F, 1.7F, 0.8F, 0.05F),
    SPORTS(0.8F, 1.5F, 2.2F, 0.9F, 0.15F),
    OFF_ROAD(1.2F, 0.9F, 1.1F, 0.8F, 0.2F);

    private final float roadFrictionFactor;
    private final float dirtFrictionFactor;
    private final float snowFrictionFactor;
    private final float baseTraction;
    private final float slideTraction;

    WheelType(float roadFrictionFactor, float dirtFrictionFactor, float snowFrictionFactor, float baseTraction, float slideTraction)
    {
        this.roadFrictionFactor = roadFrictionFactor;
        this.dirtFrictionFactor = dirtFrictionFactor;
        this.snowFrictionFactor = snowFrictionFactor;
        this.baseTraction = baseTraction;
        this.slideTraction = slideTraction;
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

    @Override
    public float getBaseTraction()
    {
        return this.baseTraction;
    }

    @Override
    public float getSlideTraction()
    {
        return this.slideTraction;
    }
}