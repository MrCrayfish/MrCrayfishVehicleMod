package com.mrcrayfish.vehicle.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * Author: MrCrayfish
 */
public enum EngineType
{
    NONE,
    SMALL_MOTOR,
    LARGE_MOTOR,
    ELECTRIC_MOTOR;

    public static EngineType getType(ItemStack stack)
    {
        return getType(stack.getItemDamage());
    }

    public static EngineType getType(int index)
    {
        if(index < 0 || index >= values().length)
        {
            return NONE;
        }
        return EngineType.values()[index];
    }
}
