package com.mrcrayfish.vehicle.recipe;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class RecipeRefillSprayCan extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    public RecipeRefillSprayCan()
    {
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "refill_spray_can"));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        ItemStack foundSprayCan = ItemStack.EMPTY;
        ItemStack foundEmptySprayCan = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof ItemSprayCan)
                {
                    if(ItemSprayCan.createTagCompound(stack).hasKey("color"))
                    {
                        if (!foundSprayCan.isEmpty())
                        {
                            return false;
                        }
                        foundSprayCan = stack;
                    }
                    else
                    {
                        if (!foundEmptySprayCan.isEmpty())
                        {
                            return false;
                        }
                        foundEmptySprayCan = stack;
                    }
                }
            }
        }
        return !foundSprayCan.isEmpty() && !foundEmptySprayCan.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack foundSprayCan = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof ItemSprayCan)
                {
                    if(ItemSprayCan.createTagCompound(stack).hasKey("color"))
                    {
                        foundSprayCan = stack;
                    }
                }
            }
        }

        if(!foundSprayCan.isEmpty())
        {
            ItemStack copy = foundSprayCan.copy();
            NBTTagCompound tagCompound = ItemSprayCan.createTagCompound(copy);
            tagCompound.setInteger("remainingSprays", ItemSprayCan.MAX_SPRAYS);
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
}
