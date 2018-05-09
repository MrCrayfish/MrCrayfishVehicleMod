package com.mrcrayfish.vehicle.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public enum EngineType
{
    POOR(0.75F, -2F, 0F),
    BASIC(1.0F, 0F, 0F),
    ADVANCED(1.25F, 1F, 0F),
    STREET(1.5F, 3F, 0F),
    RACING(1.1F, 6F, 0F);

    float accelerationMultiplier;
    float additionalMaxSpeed;
    float fuelConsumption;

    EngineType(float accelerationMultiplier, float additionalMaxSpeed, float fuelConsumption)
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

    public static EngineType getType(ItemStack stack)
    {
        return getType(stack.getItemDamage());
    }

    public static EngineType getType(int index)
    {
        if(index < 0 || index >= values().length)
        {
            return POOR;
        }
        return EngineType.values()[index];
    }
}
