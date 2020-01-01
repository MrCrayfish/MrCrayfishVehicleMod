package com.mrcrayfish.vehicle.crafting;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Author: MrCrayfish
 */
public class FluidExtract
{
    private Fluid fluid;
    private int amount;

    public FluidExtract(Fluid fluid, int amount)
    {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid()
    {
        return this.fluid;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public FluidStack createStack()
    {
        return new FluidStack(this.fluid, this.amount);
    }
}
