package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkstationIngredient extends Ingredient
{
    private final int count;

    protected WorkstationIngredient(Stream<? extends IItemList> itemList, int count)
    {
        super(itemList);
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
        Ingredient ingredient = Ingredient.fromValues(Stream.of(valueFromJson(object)));
        Ingredient.IItemList[] values = getValues(ingredient);
        int count = JSONUtils.getAsInt(object, "count", 1);
        return new WorkstationIngredient(Stream.of(values), count);
    }

    private static Ingredient.IItemList[] getValues(Ingredient ingredient)
    {
        return ObfuscationReflectionHelper.getPrivateValue(Ingredient.class, ingredient, "field_199807_b");
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
            System.out.println("Reading ingredients from server!");
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
}
