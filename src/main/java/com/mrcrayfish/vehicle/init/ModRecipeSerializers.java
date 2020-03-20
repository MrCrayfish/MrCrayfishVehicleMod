package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.VehicleRecipeSerializer;
import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
public class ModRecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

    public static final RegistryObject<SpecialRecipeSerializer<RecipeColorSprayCan>> COLOR_SPRAY_CAN = RECIPE_SERIALIZERS.register("color_spray_can", () -> new SpecialRecipeSerializer<>(RecipeColorSprayCan::new));
    public static final RegistryObject<SpecialRecipeSerializer<RecipeRefillSprayCan>> REFILL_SPRAY_CAN = RECIPE_SERIALIZERS.register("refill_spray_can", () -> new SpecialRecipeSerializer<>(RecipeRefillSprayCan::new));
    public static final RegistryObject<FluidExtractorRecipeSerializer> FLUID_EXTRACTOR = RECIPE_SERIALIZERS.register("fluid_extractor", FluidExtractorRecipeSerializer::new);
    public static final RegistryObject<FluidMixerRecipeSerializer> FLUID_MIXER = RECIPE_SERIALIZERS.register("fluid_mixer", FluidMixerRecipeSerializer::new);
    public static final RegistryObject<VehicleRecipeSerializer> CRAFTING = RECIPE_SERIALIZERS.register("crafting", VehicleRecipeSerializer::new);
}