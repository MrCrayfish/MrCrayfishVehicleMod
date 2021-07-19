package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipeBuilder
{
    private final IRecipeSerializer<?> serializer;
    private final Ingredient ingredient;
    private final FluidEntry entry;

    public FluidExtractorRecipeBuilder(IRecipeSerializer<?> serializer, Ingredient ingredient, FluidEntry entry)
    {
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.entry = entry;
    }

    public static FluidExtractorRecipeBuilder extracting(Ingredient ingredient, FluidEntry entry)
    {
        return new FluidExtractorRecipeBuilder(ModRecipeSerializers.FLUID_EXTRACTOR.get(), ingredient, entry);
    }

    public void save(Consumer<IFinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.ingredient, this.entry));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final IRecipeSerializer<?> serializer;
        private final Ingredient ingredient;
        private final FluidEntry entry;

        public Result(ResourceLocation id, IRecipeSerializer<?> serializer, Ingredient ingredient, FluidEntry entry)
        {
            this.id = id;
            this.serializer = serializer;
            this.ingredient = ingredient;
            this.entry = entry;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            object.add("ingredient", this.ingredient.toJson());
            object.add("result", this.entry.toJson());
        }

        @Override
        public ResourceLocation getId()
        {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getType()
        {
            return this.serializer;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement()
        {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId()
        {
            return null;
        }
    }
}
