package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class VehicleRecipe implements IRecipe<WorkstationTileEntity>
{
    private ResourceLocation id;
    private EntityType<?> vehicle;
    private ImmutableList<ItemStack> materials;

    public VehicleRecipe(ResourceLocation id, EntityType<?> vehicle, ImmutableList<ItemStack> materials)
    {
        this.id = id;
        this.vehicle = vehicle;
        this.materials = materials;
    }

    public EntityType<?> getVehicle()
    {
        return vehicle;
    }

    public ImmutableList<ItemStack> getMaterials()
    {
        return materials;
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
        return ModRecipeSerializers.CRAFTING.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return RecipeType.CRAFTING;
    }
}
