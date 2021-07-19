package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidExtractorRecipe>
{
    @Override
    public FluidExtractorRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {
        if(!json.has("ingredient"))
        {
            throw new com.google.gson.JsonSyntaxException("Missing ingredient, expected to find a item");
        }
        ItemStack ingredient = CraftingHelper.getItemStack(json.getAsJsonObject("ingredient"), false);
        if(!json.has("result"))
        {
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a fluid entry");
        }
        FluidEntry result = FluidEntry.fromJson(json.getAsJsonObject("result"));
        return new FluidExtractorRecipe(recipeId, ingredient, result);
    }

    @Nullable
    @Override
    public FluidExtractorRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
    {
        ItemStack ingredient = buffer.readItem();
        FluidEntry result = FluidEntry.read(buffer);
        return new FluidExtractorRecipe(recipeId, ingredient, result);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, FluidExtractorRecipe recipe)
    {
        buffer.writeItem(recipe.getIngredient());
        recipe.getResult().write(buffer);
    }
}
