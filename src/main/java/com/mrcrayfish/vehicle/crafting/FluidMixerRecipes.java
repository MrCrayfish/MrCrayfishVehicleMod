package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
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
        this.mixingMap.put(new FluidMixerRecipe(ModFluids.BLAZE_JUICE, 20, ModFluids.ENDER_SAP, 20, new ItemStack(Items.GLOWSTONE_DUST)), new FluidExtract(ModFluids.FUELIUM, 40));
    }

    public HashMap<FluidMixerRecipe, FluidExtract> getMixingMap()
    {
        return this.mixingMap;
    }

    @Nullable
    public FluidMixerRecipe getRecipe(Fluid fluidOne, Fluid fluidTwo, ItemStack ingredient)
    {
        int hashCode = Objects.hash(fluidOne, fluidTwo.hashCode(), ingredient.getItem());
        Optional<FluidMixerRecipe> optional = this.mixingMap.keySet().stream().filter(fluidMixerRecipe -> fluidMixerRecipe.hashCode() == hashCode).findFirst();
        return optional.orElse(null);
    }

    public boolean isIngredient(ItemStack ingredient)
    {
        return this.mixingMap.keySet().stream().anyMatch(recipe -> InventoryUtil.areItemStacksEqualIgnoreCount(recipe.getIngredient(), ingredient));
    }

    @Nullable
    public FluidExtract getRecipeResult(FluidMixerRecipe recipe)
    {
        return this.mixingMap.get(recipe);
    }
}
