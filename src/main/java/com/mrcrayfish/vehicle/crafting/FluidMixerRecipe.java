package com.mrcrayfish.vehicle.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipe
{
    private Fluid[] fluids;
    private int[] amounts;
    private ItemStack ingredient;

    public FluidMixerRecipe(Fluid fluidOne, int fluidOneAmount, Fluid fluidTwo, int fluidTwoAmount, ItemStack ingredient)
    {
        this.fluids = new Fluid[] { fluidOne, fluidTwo };
        this.amounts = new int[] { fluidOneAmount, fluidTwoAmount };
        this.ingredient = ingredient;
    }

    public Fluid[] getFluids()
    {
        return fluids;
    }

    public int[] getAmounts()
    {
        return amounts;
    }

    public ItemStack getIngredient()
    {
        return ingredient;
    }

    public int getFluidAmount(Fluid fluid)
    {
        for(int i = 0; i < 2; i++)
        {
            if(fluids[i].equals(fluid))
            {
                return amounts[i];
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
                if(other.fluids[i].equals(fluids[j]))
                {
                    matchCount++;
                }
            }
        }
        return matchCount == 2 && areItemStacksEqual(other.ingredient, ingredient);
    }

    @Override
    public int hashCode()
    {
        return fluids[0].hashCode() + fluids[1].hashCode() + ingredient.getItem().hashCode() + ingredient.getItemDamage();
    }

    public static boolean areItemStacksEqual(ItemStack stack, ItemStack other)
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

    public boolean requiresFluid(Fluid fluid)
    {
        for(Fluid f : fluids)
        {
            if(f.equals(fluid))
            {
                return true;
            }
        }
        return false;
    }
}
