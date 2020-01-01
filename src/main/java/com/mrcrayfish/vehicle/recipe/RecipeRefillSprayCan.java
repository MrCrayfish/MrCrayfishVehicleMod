package com.mrcrayfish.vehicle.recipe;

import com.mrcrayfish.vehicle.init.ModRecipes;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class RecipeRefillSprayCan extends SpecialRecipe
{
    public RecipeRefillSprayCan(ResourceLocation id)
    {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World worldIn)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return false;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return false;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }
        return !sprayCan.isEmpty() && !emptySprayCan.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }

        if(!sprayCan.isEmpty() && !emptySprayCan.isEmpty())
        {
            ItemStack copy = sprayCan.copy();
            ((SprayCanItem) copy.getItem()).refill(copy);
            return copy;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModRecipes.REFILL_SPRAY_CAN;
    }
}
