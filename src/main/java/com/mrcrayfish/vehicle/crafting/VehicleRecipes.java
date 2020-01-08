package com.mrcrayfish.vehicle.crafting;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class VehicleRecipes
{
    @Nullable
    public static VehicleRecipe getRecipe(EntityType<?> entityType, World world)
    {
        List<VehicleRecipe> recipes = world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == RecipeType.CRAFTING).map(recipe -> (VehicleRecipe) recipe).collect(Collectors.toList());
        return recipes.stream().filter(recipe -> recipe.getVehicle() == entityType).findFirst().orElse(null);
    }
}
