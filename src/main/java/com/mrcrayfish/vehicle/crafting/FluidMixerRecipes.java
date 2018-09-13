package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
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
        mixingMap.put(new FluidMixerRecipe(ModFluids.BLAZE_JUICE, 20, ModFluids.ENDER_SAP, 20, new ItemStack(Items.GLOWSTONE_DUST)), new FluidExtract(ModFluids.FUELIUM, 40));
    }

    @Nullable
    public FluidMixerRecipe getRecipe(Fluid fluidOne, Fluid fluidTwo, ItemStack ingredient)
    {
        int hashCode = fluidOne.hashCode() + fluidTwo.hashCode() + ingredient.getItem().hashCode() + ingredient.getItemDamage();
        Optional<FluidMixerRecipe> optional = mixingMap.keySet().stream().filter(fluidMixerRecipe -> fluidMixerRecipe.hashCode() == hashCode).findFirst();
        return optional.orElse(null);
    }

    @Nullable
    public FluidExtract getRecipeResult(FluidMixerRecipe recipe)
    {
        return mixingMap.get(recipe);
    }
}
