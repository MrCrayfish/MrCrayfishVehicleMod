package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipeSerializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipeSerializers
{
    private static final List<IRecipeSerializer> RECIPES = new ArrayList<>();

    public static final FluidExtractorRecipeSerializer FLUID_EXTRACTOR = register("vehicle:fluid_extractor", new FluidExtractorRecipeSerializer());
    public static final FluidMixerRecipeSerializer FLUID_MIXER = register("vehicle:fluid_mixer", new FluidMixerRecipeSerializer());

    private static <T extends IRecipeSerializer<? extends IRecipe<?>>> T register(String name, T t)
    {
        t.setRegistryName(new ResourceLocation(name));
        RECIPES.add(t);
        return t;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerItems(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        RECIPES.forEach(serializer -> event.getRegistry().register(serializer));
        RECIPES.clear();
    }
}
