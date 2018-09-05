package com.mrcrayfish.vehicle.crafting;

import net.minecraftforge.fluids.Fluid;
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
        return fluid;
    }

    public int getAmount()
    {
        return amount;
    }

    public FluidStack createStack()
    {
        return new FluidStack(fluid, amount);
    }
}
