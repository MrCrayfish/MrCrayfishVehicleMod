package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipes
{
    private static final FluidMixerRecipes INSTANCE = new FluidMixerRecipes();

    public static FluidMixerRecipes getInstance()
    {
        return INSTANCE;
    }

    private final HashMap<FluidMixerRecipe, FluidExtract> mixingMap = Maps.newHashMap();

    private FluidMixerRecipes()
    {
        mixingMap.put(new FluidMixerRecipe(ModFluids.BLAZE_JUICE, 200, ModFluids.ENDER_SAP, 200, new ItemStack(Items.GLOWSTONE_DUST)), new FluidExtract(ModFluids.FUELIUM, 400));
    }

    public HashMap<FluidMixerRecipe, FluidExtract> getMixingMap()
    {
        return mixingMap;
    }

    @Nullable
    public FluidMixerRecipe getRecipe(Fluid fluidOne, Fluid fluidTwo, ItemStack ingredient)
    {
        int hashCode = fluidOne.hashCode() + fluidTwo.hashCode() + ingredient.getItem().hashCode() + ingredient.getItemDamage();
        Optional<FluidMixerRecipe> optional = mixingMap.keySet().stream().filter(fluidMixerRecipe -> fluidMixerRecipe.hashCode() == hashCode).findFirst();
        return optional.orElse(null);
    }

    public boolean isIngredient(ItemStack ingredient)
    {
        return mixingMap.keySet().stream().anyMatch(recipe -> FluidMixerRecipe.areItemStacksEqual(recipe.getIngredient(), ingredient));
    }

    @Nullable
    public FluidExtract getRecipeResult(FluidMixerRecipe recipe)
    {
        return mixingMap.get(recipe);
    }
}
