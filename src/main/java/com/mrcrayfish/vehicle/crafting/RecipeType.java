package com.mrcrayfish.vehicle.crafting;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Author: MrCrayfish
 */
public class RecipeType
{
    public static final IRecipeType<FluidExtractorRecipe> FLUID_EXTRACTOR = register("vehicle:fluid_extractor");
    public static final IRecipeType<FluidMixerRecipe> FLUID_MIXER = register("vehicle:fluid_mixer");
    public static final IRecipeType<WorkstationRecipe> WORKSTATION = register("vehicle:workstation");

    static <T extends IRecipe<?>> IRecipeType<T> register(final String key)
    {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new IRecipeType<T>()
        {
            @Override
            public String toString()
            {
                return key;
            }
        });
    }
}
