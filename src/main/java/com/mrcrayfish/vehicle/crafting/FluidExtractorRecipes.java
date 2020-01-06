package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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

    private final ImmutableMap<ItemStack, FluidEntry> extractingMap;

    private FluidExtractorRecipes()
    {
        ImmutableMap.Builder<ItemStack, FluidEntry> builder = new ImmutableMap.Builder<>();
        builder.put(new ItemStack(Items.ENDER_PEARL), new FluidEntry(ModFluids.ENDER_SAP, 500));
        builder.put(new ItemStack(Items.BLAZE_ROD), new FluidEntry(ModFluids.BLAZE_JUICE, 350));
        extractingMap = builder.build();
    }

    public ImmutableMap<ItemStack, FluidEntry> getExtractingMap()
    {
        return extractingMap;
    }

    @Nullable
    public FluidEntry getRecipeResult(ItemStack stack)
    {
        for(Map.Entry<ItemStack, FluidEntry> entry : extractingMap.entrySet())
        {
            if(InventoryUtil.areItemStacksEqualIgnoreCount(stack, entry.getKey()))
            {
                return entry.getValue();
            }
        }
        return null;
    }
}
