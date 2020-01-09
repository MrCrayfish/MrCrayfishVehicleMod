package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class VehicleRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<VehicleRecipe>
{
    @Override
    public VehicleRecipe read(ResourceLocation recipeId, JsonObject json)
    {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        JsonArray input = JSONUtils.getJsonArray(json, "materials");
        for(int i = 0; i < input.size(); i++)
        {
            JsonObject itemObject = input.get(i).getAsJsonObject();
            String itemName = JSONUtils.getString(itemObject, "item");
            ResourceLocation id = new ResourceLocation(itemName);
            if(id.getNamespace().equals("cfm") && !ModList.get().isLoaded("cfm"))
                continue;
            ItemStack stack = CraftingHelper.getItemStack(itemObject, false);
            builder.add(stack);
        }
        if(!json.has("vehicle"))
        {
            throw new com.google.gson.JsonSyntaxException("Missing vehicle entry");
        }
        ResourceLocation vehicle = new ResourceLocation(JSONUtils.getString(json, "vehicle"));
        Optional<EntityType<?>> optional = EntityType.byKey(JSONUtils.getString(json, "vehicle"));
        if(!optional.isPresent())
        {
            throw new com.google.gson.JsonSyntaxException("Invalid vehicle entity: " + vehicle.toString());
        }
        return new VehicleRecipe(recipeId, optional.get(), builder.build());
    }

    @Nullable
    @Override
    public VehicleRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
    {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(buffer.readResourceLocation());
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        int size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
        {
            builder.add(readItemStack(buffer));
        }
        return new VehicleRecipe(recipeId, entityType, builder.build());
    }

    @Override
    public void write(PacketBuffer buffer, VehicleRecipe recipe)
    {
        buffer.writeResourceLocation(recipe.getVehicle().getRegistryName());
        buffer.writeVarInt(recipe.getMaterials().size());
        for(ItemStack stack : recipe.getMaterials())
        {
            writeItemStack(buffer, stack);
        }
    }

    private static void writeItemStack(PacketBuffer buffer, ItemStack stack)
    {
        if(stack.isEmpty())
        {
            buffer.writeBoolean(false);
        }
        else
        {
            buffer.writeBoolean(true);
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getIdFromItem(item));
            buffer.writeVarInt(stack.getCount());
            buffer.writeCompoundTag(stack.getTag());
        }
    }

    private static ItemStack readItemStack(PacketBuffer buffer)
    {
        if(buffer.readBoolean())
        {
            int id = buffer.readVarInt();
            int count = buffer.readVarInt();
            ItemStack itemstack = new ItemStack(Item.getItemById(id), count);
            itemstack.readShareTag(buffer.readCompoundTag());
            return itemstack;
        }
        return ItemStack.EMPTY;
    }
}
