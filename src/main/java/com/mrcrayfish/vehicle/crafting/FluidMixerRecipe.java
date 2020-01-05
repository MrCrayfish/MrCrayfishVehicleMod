package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipe
{
    private Fluid[] fluids;
    private int[] amounts;
    private ItemStack ingredient;
    private int hashCode;

    public FluidMixerRecipe(Fluid fluidOne, int fluidOneAmount, Fluid fluidTwo, int fluidTwoAmount, ItemStack ingredient)
    {
        this.fluids = new Fluid[]{fluidOne, fluidTwo};
        this.amounts = new int[]{fluidOneAmount, fluidTwoAmount};
        this.ingredient = ingredient;
    }

    public Fluid[] getFluids()
    {
        return this.fluids;
    }

    public int[] getAmounts()
    {
        return this.amounts;
    }

    public ItemStack getIngredient()
    {
        return this.ingredient;
    }

    public int getFluidAmount(Fluid fluid)
    {
        for(int i = 0; i < 2; i++)
        {
            if(this.fluids[i].equals(fluid))
            {
                return this.amounts[i];
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof FluidMixerRecipe)) return false;
        FluidMixerRecipe other = (FluidMixerRecipe) obj;
        int matchCount = 0;
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                if(other.fluids[i].equals(this.fluids[j]))
                {
                    matchCount++;
                }
            }
        }
        return matchCount == 2 && InventoryUtil.areItemStacksEqualIgnoreCount(other.ingredient, this.ingredient);
    }

    @Override
    public int hashCode()
    {
        if(this.hashCode == 0)
        {
            this.hashCode = Objects.hash(this.fluids[0].getRegistryName(), this.fluids[1].getRegistryName(), this.ingredient.getItem().getRegistryName());
        }
        return this.hashCode;
    }

    public boolean requiresFluid(Fluid fluid)
    {
        for(Fluid requiredFluid : this.fluids)
        {
            if(requiredFluid.equals(fluid))
            {
                return true;
            }
        }
        return false;
    }
}
