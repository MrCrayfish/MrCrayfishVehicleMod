package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipes
{
    private static final FluidExtractorRecipes INSTANCE = new FluidExtractorRecipes();

    public static FluidExtractorRecipes getInstance()
    {
        return INSTANCE;
    }

    private final ImmutableMap<ItemStack, FluidExtract> extractingMap;

    private FluidExtractorRecipes()
    {
        ImmutableMap.Builder<ItemStack, FluidExtract> builder = new ImmutableMap.Builder<>();
        builder.put(new ItemStack(Items.ENDER_PEARL), new FluidExtract(ModFluids.ENDER_SAP, 600));
        builder.put(new ItemStack(Items.BLAZE_ROD), new FluidExtract(ModFluids.BLAZE_JUICE, 450));
        extractingMap = builder.build();
    }

    public ImmutableMap<ItemStack, FluidExtract> getExtractingMap()
    {
        return extractingMap;
    }

    @Nullable
    public FluidExtract getRecipeResult(ItemStack stack)
    {
        for(Map.Entry<ItemStack, FluidExtract> entry : extractingMap.entrySet())
        {
            if(areItemStacksEqual(stack, entry.getKey()))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    private static boolean areItemStacksEqual(ItemStack stack, ItemStack other)
    {
        if (stack.getItem() != other.getItem())
        {
            return false;
        }
        else if (stack.getItemDamage() != other.getItemDamage())
        {
            return false;
        }
        else if(stack.getTagCompound() == null && other.getTagCompound() != null)
        {
            return false;
        }
        else
        {
            return (stack.getTagCompound() == null || stack.getTagCompound().equals(other.getTagCompound()));
        }
    }
}
