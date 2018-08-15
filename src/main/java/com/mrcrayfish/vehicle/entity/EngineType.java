package com.mrcrayfish.vehicle.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

/**
 * Author: MrCrayfish
 */
public enum EngineType
{
    POOR(0.75F, -2F, 0F, "wood", TextFormatting.WHITE),
    BASIC(1.0F, 0F, 0F, "stone", TextFormatting.DARK_GRAY),
    ADVANCED(1.25F, 1F, 0F, "iron", TextFormatting.GRAY),
    STREET(1.5F, 3F, 0F, "gold", TextFormatting.GOLD),
    RACING(1.1F, 6F, 0F, "diamond", TextFormatting.AQUA);

    float accelerationMultiplier;
    float additionalMaxSpeed;
    float fuelConsumption;

    String tierName;
    TextFormatting tierColor;

    EngineType(float accelerationMultiplier, float additionalMaxSpeed, float fuelConsumption, String tierName, TextFormatting tierColor)
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
