package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.crafting.FluidMixerRecipes;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class FluidTankMixerInput extends FluidTank
{
    public FluidTankMixerInput(int capacity)
    {
        super(capacity, stack -> FluidMixerRecipes.getInstance().getMixingMap().keySet().stream().anyMatch(fluidMixerRecipe -> fluidMixerRecipe.requiresFluid(stack.getFluid())));
    }
}
