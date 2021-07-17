package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipe implements IRecipe<FluidMixerTileEntity>
{
    private ResourceLocation id;
    private FluidEntry[] inputs;
    private ItemStack ingredient;
    private FluidEntry result;
    private int hashCode;

    public FluidMixerRecipe(ResourceLocation id, FluidEntry fluidOne, FluidEntry fluidTwo, ItemStack ingredient, FluidEntry result)
    {
        this.id = id;
        this.inputs = new FluidEntry[]{fluidOne, fluidTwo};
        this.ingredient = ingredient;
        this.result = result;
    }

    public FluidEntry[] getInputs()
    {
        return inputs;
    }

    public ItemStack getIngredient()
    {
        return this.ingredient;
    }

    public FluidEntry getResult()
    {
        return result;
    }

    public int getFluidAmount(Fluid fluid)
    {
        for(int i = 0; i < 2; i++)
        {
            FluidEntry entry = this.inputs[i];
            if(entry.getFluid().equals(fluid))
            {
                return entry.getAmount();
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof FluidMixerRecipe)) return false;
        FluidMixerRecipe other = (FluidMixerRecipe) obj;
        int index = -1;
        for(int i = 0; i < 2; i++)
        {
            if(other.inputs[0].getFluid().equals(this.inputs[i].getFluid()))
            {
                index = i == 1 ? 0 : 1;
            }
        }
        if(index == -1) return false;
        if(!other.inputs[1].getFluid().equals(this.inputs[index].getFluid())) return false;
        return InventoryUtil.areItemStacksEqualIgnoreCount(other.ingredient, this.ingredient);
    }

    @Override
    public int hashCode()
    {
        if(this.hashCode == 0)
        {
            this.hashCode = Objects.hash(this.inputs[0].getFluid().getRegistryName(), this.inputs[1].getFluid().getRegistryName(), this.ingredient.getItem().getRegistryName());
        }
        return this.hashCode;
    }

    @Override
    public boolean matches(FluidMixerTileEntity fluidMixer, World worldIn)
    {
        if(fluidMixer.getEnderSapTank().isEmpty() || fluidMixer.getBlazeTank().isEmpty())
            return false;
        Fluid inputOne = fluidMixer.getEnderSapTank().getFluid().getFluid();
        int index = -1;
        for(int i = 0; i < 2; i++)
        {
            if(inputOne.equals(this.inputs[i].getFluid()))
            {
                index = i == 1 ? 0 : 1;
            }
        }
        if(index == -1) return false;
        Fluid inputTwo = fluidMixer.getBlazeTank().getFluid().getFluid();
        if(!inputTwo.equals(this.inputs[index].getFluid())) return false;
        return InventoryUtil.areItemStacksEqualIgnoreCount(fluidMixer.getItem(FluidMixerTileEntity.SLOT_INGREDIENT), this.ingredient);
    }

    @Override
    public ItemStack assemble(FluidMixerTileEntity inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.FLUID_MIXER.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return RecipeType.FLUID_MIXER;
    }
}