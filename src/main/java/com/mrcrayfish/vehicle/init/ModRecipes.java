package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;

/**
 * Author: MrCrayfish
 */
public class ModRecipes
{
    public static void register()
    {
        RegistrationHandler.Recipes.add(new RecipeColorSprayCan());
        RegistrationHandler.Recipes.add(new RecipeRefillSprayCan());
    }
}
