package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipe implements IRecipe<WorkstationTileEntity>
{
    private ResourceLocation id;
    private EntityType<?> vehicle;
    private ImmutableList<WorkstationIngredient> materials;

    public WorkstationRecipe(ResourceLocation id, EntityType<?> vehicle, ImmutableList<WorkstationIngredient> materials)
    {
        this.id = id;
        this.vehicle = vehicle;
        this.materials = materials;
    }

    public EntityType<?> getVehicle()
    {
        return this.vehicle;
    }

    public ImmutableList<WorkstationIngredient> getMaterials()
    {
        return this.materials;
    }

    @Override
    public boolean matches(WorkstationTileEntity inv, World worldIn)
    {
        return false;
    }

    @Override
    public ItemStack assemble(WorkstationTileEntity inv)
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
        return ModRecipeSerializers.WORKSTATION.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return RecipeType.WORKSTATION;
    }

    public boolean hasMaterials(PlayerEntity player)
    {
        for(WorkstationIngredient ingredient : this.getMaterials())
        {
            if(!InventoryUtil.hasWorkstationIngredient(player, ingredient))
            {
                return false;
            }
        }
        return true;
    }

    public void consumeMaterials(PlayerEntity player)
    {
        for(WorkstationIngredient ingredient : this.getMaterials())
        {
            InventoryUtil.removeWorkstationIngredient(player, ingredient);
        }
    }
}
