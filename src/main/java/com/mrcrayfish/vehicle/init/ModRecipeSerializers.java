package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipeSerializer;
import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModRecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

    public static final RegistryObject<SpecialRecipeSerializer<RecipeColorSprayCan>> COLOR_SPRAY_CAN = REGISTER.register("color_spray_can", () -> new SpecialRecipeSerializer<>(RecipeColorSprayCan::new));
    public static final RegistryObject<SpecialRecipeSerializer<RecipeRefillSprayCan>> REFILL_SPRAY_CAN = REGISTER.register("refill_spray_can", () -> new SpecialRecipeSerializer<>(RecipeRefillSprayCan::new));
    public static final RegistryObject<FluidExtractorRecipeSerializer> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", FluidExtractorRecipeSerializer::new);
    public static final RegistryObject<FluidMixerRecipeSerializer> FLUID_MIXER = REGISTER.register("fluid_mixer", FluidMixerRecipeSerializer::new);
    public static final RegistryObject<WorkstationRecipeSerializer> WORKSTATION = REGISTER.register("workstation", WorkstationRecipeSerializer::new);
}