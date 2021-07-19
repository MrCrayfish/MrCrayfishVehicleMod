package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeBuilder
{
    private final IRecipeSerializer<?> serializer;
    private final ResourceLocation entityId;
    private final List<WorkstationIngredient> ingredients;
    private final List<ICondition> conditions = new ArrayList<>();

    public WorkstationRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        this.serializer = serializer;
        this.entityId = entityId;
        this.ingredients = ingredients;
    }

    public static WorkstationRecipeBuilder crafting(ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        return new WorkstationRecipeBuilder(ModRecipeSerializers.WORKSTATION.get(), entityId, ingredients);
    }

    public WorkstationRecipeBuilder addCondition(ICondition condition)
    {
        this.conditions.add(condition);
        return this;
    }

    public void save(Consumer<IFinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.entityId, this.ingredients, this.conditions));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final ResourceLocation entityId;
        private final List<WorkstationIngredient> ingredients;
        private final List<ICondition> conditions;
        private final IRecipeSerializer<?> serializer;

        private Result(ResourceLocation id, IRecipeSerializer<?> serializer, ResourceLocation entityId, List<WorkstationIngredient> ingredients, List<ICondition> conditions)
        {
            this.id = id;
            this.serializer = serializer;
            this.entityId = entityId;
            this.ingredients = ingredients;
            this.conditions = conditions;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            object.addProperty("vehicle", this.entityId.toString());

            JsonArray conditions = new JsonArray();
            this.conditions.forEach(condition -> conditions.add(CraftingHelper.serialize(condition)));
            if(conditions.size() > 0)
            {
                object.add("conditions", conditions);
            }

            JsonArray materials = new JsonArray();
            this.ingredients.forEach(ingredient -> materials.add(ingredient.toJson()));
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
