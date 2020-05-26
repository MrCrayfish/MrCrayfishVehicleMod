package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;

/**
 * Author: MrCrayfish
 */
public abstract class BlazeJuice extends ModFluid
{
    public BlazeJuice()
    {
        super(ModFluids.BLAZE_JUICE, ModFluids.FLOWING_BLAZE_JUICE, ModBlocks.BLAZE_JUICE, 1000, 800, 0.5F, 254, 198, 0);
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.BLAZE_JUICE_BUCKET.get();
    }

    public static class Source extends BlazeJuice
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

    public static class Flowing extends BlazeJuice
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
