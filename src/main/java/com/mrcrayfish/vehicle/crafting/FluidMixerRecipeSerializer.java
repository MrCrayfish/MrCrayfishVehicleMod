package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidMixerRecipe>
{
    @Override
    public FluidMixerRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {
        String s = JSONUtils.getAsString(json, "group", "");
        JsonArray input = JSONUtils.getAsJsonArray(json, "input");
        if(input.size() != 2)
        {
            throw new com.google.gson.JsonSyntaxException("Invalid input, must only have two objects");
        }
        FluidEntry inputOne = FluidEntry.fromJson(input.get(0).getAsJsonObject());
        FluidEntry inputTwo = FluidEntry.fromJson(input.get(1).getAsJsonObject());
        ItemStack ingredient = CraftingHelper.getItemStack(json.getAsJsonObject("ingredient"), false);
        FluidEntry result = FluidEntry.fromJson(json.getAsJsonObject("result"));
        return new FluidMixerRecipe(recipeId, inputOne, inputTwo, ingredient, result);
    }

    @Nullable
    @Override
    public FluidMixerRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
    {
        FluidEntry inputOne = FluidEntry.read(buffer);
        FluidEntry inputTwo = FluidEntry.read(buffer);
        ItemStack ingredient = buffer.readItem();
        FluidEntry result = FluidEntry.read(buffer);
        return new FluidMixerRecipe(recipeId, inputOne, inputTwo, ingredient, result);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, FluidMixerRecipe recipe)
    {
        for(FluidEntry entry : recipe.getInputs())
        {
            entry.write(buffer);
        }
        buffer.writeItem(recipe.getIngredient());
        recipe.getResult().write(buffer);
    }
}
