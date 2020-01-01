package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModRecipes
{
    public static final SpecialRecipeSerializer<RecipeColorSprayCan> COLOR_SPRAY_CAN = null;
    public static final SpecialRecipeSerializer<RecipeRefillSprayCan> REFILL_SPRAY_CAN = null;

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeColorSprayCan::new).setRegistryName(new ResourceLocation(Reference.MOD_ID, "color_spray_can")));
        event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeRefillSprayCan::new).setRegistryName(new ResourceLocation(Reference.MOD_ID, "refill_spray_can")));
    }
}
