package com.mrcrayfish.vehicle.entity;

import net.minecraft.util.text.TextFormatting;

/**
 * Author: MrCrayfish
 */
public enum EngineTier
{
    IRON(1.25F, 1F, 0F, "iron", TextFormatting.GRAY),
    GOLD(1.5F, 3F, 0F, "gold", TextFormatting.GOLD),
    DIAMOND(1.1F, 6F, 0F, "diamond", TextFormatting.AQUA);

    float accelerationMultiplier;
    float additionalMaxSpeed;
    float fuelConsumption;
    String tierName;
    TextFormatting tierColor;

    EngineTier(float accelerationMultiplier, float additionalMaxSpeed, float fuelConsumption, String tierName, TextFormatting tierColor)
    {
        this.accelerationMultiplier = accelerationMultiplier;
        this.additionalMaxSpeed = additionalMaxSpeed;
        this.fuelConsumption = fuelConsumption;
        this.tierName = tierName;
        this.tierColor = tierColor;
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

    public String getTierName()
    {
        return tierName;
    }

    public TextFormatting getTierColor()
    {
        return tierColor;
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
