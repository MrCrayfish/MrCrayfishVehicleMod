package com.mrcrayfish.vehicle.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.IFluidTankWriter;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

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
    protected ItemStack doApply(ItemStack stack, LootContext context)
    {
        BlockState state = context.get(LootParameters.BLOCK_STATE);
        if(state != null && stack.getItem() == state.getBlock().asItem())
        {
            TileEntity tileEntity = context.get(LootParameters.BLOCK_ENTITY);
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

    public static class Serializer extends LootFunction.Serializer<CopyFluidTanks>
    {
        public Serializer()
        {
            super(new ResourceLocation(Reference.MOD_ID, "copy_fluid_tanks"), CopyFluidTanks.class);
        }

        @Override
        public CopyFluidTanks deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            return new CopyFluidTanks(conditionsIn);
        }
    }
}
