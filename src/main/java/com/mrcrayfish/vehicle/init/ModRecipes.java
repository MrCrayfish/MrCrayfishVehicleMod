package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;

/**
 * Author: MrCrayfish
 */
public class ModRecipes
{
    public static void register()
    {
        RegistrationHandler.Recipes.add(new RecipeColorSprayCan());
    }
}
