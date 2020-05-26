package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.StateContainer;

/**
 * Author: MrCrayfish
 */
public abstract class EnderSap extends ModFluid
{
    public EnderSap()
    {
        super(ModFluids.ENDER_SAP, ModFluids.FLOWING_ENDER_SAP, ModBlocks.ENDER_SAP, ModItems.ENDER_SAP_BUCKET, 1000, 3000, 1F, 10, 93, 80);
    }

    public static class Source extends EnderSap
    {
        @Override
        public boolean isSource(IFluidState state)
        {
            return true;
        }

        @Override
        public int getLevel(IFluidState state)
        {
            return 8;
        }
    }

    public static class Flowing extends EnderSap
    {
        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
        {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(IFluidState state)
        {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(IFluidState state)
        {
            return false;
        }
    }
}
