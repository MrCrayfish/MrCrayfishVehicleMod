package com.mrcrayfish.vehicle.entity;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public String getEngineName()
    {
        return I18n.format("vehicle.engine_type." + id + ".name");
    }
}
