package com.mrcrayfish.vehicle.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class FluidFuelium extends Fluid
{
    public FluidFuelium(String fluidName, ResourceLocation still, ResourceLocation flowing, Color color)
    {
        super("", still, flowing, color);
    }
}
