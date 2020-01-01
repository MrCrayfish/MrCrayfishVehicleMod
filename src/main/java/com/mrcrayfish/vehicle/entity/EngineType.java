package com.mrcrayfish.vehicle.entity;

import net.minecraft.client.resources.I18n;

/**
 * Author: MrCrayfish
 */
public enum EngineType
{
    NONE("none"),
    SMALL_MOTOR("small"),
    LARGE_MOTOR("large"),
    ELECTRIC_MOTOR("electric");

    String id;

    EngineType(String id)
    {
        this.id = id;
    }

    public static EngineType getType(int index)
    {
        if(index < 0 || index >= values().length)
        {
            return NONE;
        }
        return EngineType.values()[index];
    }

    public String getEngineName()
    {
        return I18n.format("vehicle.engine_type." + this.id + ".name");
    }
}
