package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.crafting.FluidMixerRecipes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Author: MrCrayfish
 */
public class FluidTankMixerInput extends FluidTank
{
    public FluidTankMixerInput(int capacity)
    {
        super(capacity);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return FluidMixerRecipes.getInstance().getMixingMap().keySet().stream().anyMatch(fluidMixerRecipe -> fluidMixerRecipe.requiresFluid(fluid.getFluid()));
    }
}
