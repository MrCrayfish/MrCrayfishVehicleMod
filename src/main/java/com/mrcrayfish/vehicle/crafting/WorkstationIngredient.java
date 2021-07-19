package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkstationIngredient extends Ingredient
{
    private final IItemList itemList;
    private final int count;

    protected WorkstationIngredient(Stream<? extends IItemList> itemList, int count)
    {
        super(itemList);
        this.itemList = null;
        this.count = count;
    }

    private WorkstationIngredient(IItemList itemList, int count)
    {
        super(Stream.of(itemList));
        this.itemList = itemList;
        this.count = count;
    }

    public int getCount()
    {
        return this.count;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public static WorkstationIngredient fromJson(JsonObject object)
    {
        Ingredient.IItemList value = valueFromJson(object);
        int count = JSONUtils.getAsInt(object, "count", 1);
        return new WorkstationIngredient(Stream.of(value), count);
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = this.itemList.serialize();
        object.addProperty("count", this.count);
        return object;
    }

    public static WorkstationIngredient of(IItemProvider provider, int count)
    {
        return new WorkstationIngredient(new Ingredient.SingleItemList(new ItemStack(provider)), count);
    }

    public static WorkstationIngredient of(ItemStack stack, int count)
    {
        return new WorkstationIngredient(new Ingredient.SingleItemList(stack), count);
    }

    public static WorkstationIngredient of(ITag<Item> tag, int count)
    {
        return new WorkstationIngredient(new Ingredient.TagList(tag), count);
    }

    public static WorkstationIngredient of(ResourceLocation id, int count)
    {
        return new WorkstationIngredient(new MissingSingleItemList(id), count);
    }

    public static class Serializer implements IIngredientSerializer<WorkstationIngredient>
    {
        public static final WorkstationIngredient.Serializer INSTANCE = new WorkstationIngredient.Serializer();

        @Override
        public WorkstationIngredient parse(PacketBuffer buffer)
        {
            int itemCount = buffer.readVarInt();
            int count = buffer.readVarInt();
            Stream<Ingredient.SingleItemList> values = Stream.generate(() ->
                    new SingleItemList(buffer.readItem())).limit(itemCount);
            return new WorkstationIngredient(values, count);
        }

        @Override
        public WorkstationIngredient parse(JsonObject object)
        {
            return WorkstationIngredient.fromJson(object);
        }

        @Override
        public void write(PacketBuffer buffer, WorkstationIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.getItems().length);
            buffer.writeVarInt(ingredient.count);
            for(ItemStack stack : ingredient.getItems())
            {
                buffer.writeItem(stack);
            }
        }
    }

    //

    /**
     * Allows ability to define an ingredient from another mod without depending. Serializes the data
     * to be read by the regular {@link SingleItemList}. Only use this for generating data.
     */
    public static class MissingSingleItemList implements Ingredient.IItemList
    {
        private final ResourceLocation id;

        public MissingSingleItemList(ResourceLocation id)
        {
            this.id = id;
        }

        @Override
        public Collection<ItemStack> getItems()
        {
            return Collections.emptyList();
        }

        @Override
        public JsonObject serialize()
        {
            JsonObject object = new JsonObject();
            object.addProperty("item", this.id.toString());
            return object;
        }
    }
}
