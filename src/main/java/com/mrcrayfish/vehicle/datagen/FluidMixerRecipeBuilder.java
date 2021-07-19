package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonArray;
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
public class FluidMixerRecipeBuilder
{
    private final IRecipeSerializer<?> serializer;
    private final FluidEntry[] input;
    private final Ingredient ingredient;
    private final FluidEntry output;

    public FluidMixerRecipeBuilder(IRecipeSerializer<?> serializer, FluidEntry inputOne, FluidEntry inputTwo, Ingredient ingredient, FluidEntry output)
    {
        this.serializer = serializer;
        this.input = new FluidEntry[]{inputOne, inputTwo};
        this.ingredient = ingredient;
        this.output = output;
    }

    public static FluidMixerRecipeBuilder mixing(FluidEntry inputOne, FluidEntry inputTwo, Ingredient ingredient, FluidEntry output)
    {
        return new FluidMixerRecipeBuilder(ModRecipeSerializers.FLUID_MIXER.get(), inputOne, inputTwo, ingredient, output);
    }

    public void save(Consumer<IFinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.input, this.ingredient, this.output));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final IRecipeSerializer<?> serializer;
        private final FluidEntry[] input;
        private final Ingredient ingredient;
        private final FluidEntry output;

        private Result(ResourceLocation id, IRecipeSerializer<?> serializer, FluidEntry[] input, Ingredient ingredient, FluidEntry output)
        {
            this.id = id;
            this.serializer = serializer;
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            JsonArray inputArray = new JsonArray();
            inputArray.add(this.input[0].toJson());
            inputArray.add(this.input[1].toJson());
            object.add("input", inputArray);
            object.add("ingredient", this.ingredient.toJson());
            object.add("result", this.output.toJson());
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
