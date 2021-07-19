package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeBuilder
{
    private final IRecipeSerializer<?> serializer;
    private final EntityType<? extends VehicleEntity> type;
    private final List<WorkstationIngredient> ingredients;

    public WorkstationRecipeBuilder(IRecipeSerializer<?> serializer, EntityType<? extends VehicleEntity> type, List<WorkstationIngredient> ingredients)
    {
        this.serializer = serializer;
        this.type = type;
        this.ingredients = ingredients;
    }

    public static WorkstationRecipeBuilder crafting(EntityType<? extends VehicleEntity> type, List<WorkstationIngredient> ingredients)
    {
        return new WorkstationRecipeBuilder(ModRecipeSerializers.WORKSTATION.get(), type, ingredients);
    }

    public void save(Consumer<IFinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.type, this.ingredients));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final EntityType<? extends VehicleEntity> type;
        private final List<WorkstationIngredient> ingredients;
        private final IRecipeSerializer<?> serializer;

        public Result(ResourceLocation id, IRecipeSerializer<?> serializer, EntityType<? extends VehicleEntity> type, List<WorkstationIngredient> ingredients)
        {
            this.id = id;
            this.serializer = serializer;
            this.type = type;
            this.ingredients = ingredients;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            object.addProperty("vehicle", this.type.getRegistryName().toString());
            JsonArray materials = new JsonArray();
            this.ingredients.forEach(ingredient -> {
                materials.add(ingredient.toJson());
            });
            object.add("materials", materials);
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
