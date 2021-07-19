package com.mrcrayfish.vehicle.world.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.init.ModLootFunctions;
import com.mrcrayfish.vehicle.tileentity.IFluidTankWriter;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class CopyFluidTanks extends LootFunction
{
    private CopyFluidTanks(ILootCondition[] conditionsIn)
    {
        super(conditionsIn);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
        if(state != null && stack.getItem() == state.getBlock().asItem())
        {
            TileEntity tileEntity = context.getParamOrNull(LootParameters.BLOCK_ENTITY);
            if(tileEntity != null)
            {
                CompoundNBT tileEntityTag = new CompoundNBT();
                if(tileEntity instanceof TileFluidHandler)
                {
                    LazyOptional<IFluidHandler> handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                    handler.ifPresent(h ->
                    {
                        FluidTank tank = (FluidTank) h;
                        if(!tank.isEmpty())
                        {
                            tank.writeToNBT(tileEntityTag);
                        }
                    });
                }
                else if(tileEntity instanceof IFluidTankWriter)
                {
                    IFluidTankWriter writer = (IFluidTankWriter) tileEntity;
                    if(!writer.areTanksEmpty())
                    {
                        writer.writeTanks(tileEntityTag);
                    }
                }

                if(!tileEntityTag.isEmpty())
                {
                    CompoundNBT compound = stack.getTag();
                    if(compound == null)
                    {
                        compound = new CompoundNBT();
                    }
                    compound.put("BlockEntityTag", tileEntityTag);
                    stack.setTag(compound);
                }
            }
        }
        return stack;
    }

    @Override
    public LootFunctionType getType()
    {
        return ModLootFunctions.COPY_FLUID_TANKS;
    }

    public static CopyFluidTanks.Builder copyFluidTanks()
    {
        return new CopyFluidTanks.Builder();
    }

    public static class Builder extends LootFunction.Builder<CopyFluidTanks.Builder>
    {
        private Builder() {}

        protected CopyFluidTanks.Builder getThis()
        {
            return this;
        }

        public ILootFunction build()
        {
            return new CopyFluidTanks(this.getConditions());
        }
    }

    public static class Serializer extends LootFunction.Serializer<CopyFluidTanks>
    {
        @Override
        public CopyFluidTanks deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            return new CopyFluidTanks(conditionsIn);
        }
    }
}
