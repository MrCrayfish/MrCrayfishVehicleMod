package com.mrcrayfish.vehicle.crafting;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipes
{
    @Nullable
    public static WorkstationRecipe getRecipe(EntityType<?> entityType, World world)
    {
        List<WorkstationRecipe> recipes = world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == RecipeType.WORKSTATION).map(recipe -> (WorkstationRecipe) recipe).collect(Collectors.toList());
        return recipes.stream().filter(recipe -> recipe.getVehicle() == entityType).findFirst().orElse(null);
    }
}
