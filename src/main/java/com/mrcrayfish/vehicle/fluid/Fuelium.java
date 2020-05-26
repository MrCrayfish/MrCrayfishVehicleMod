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
public abstract class Fuelium extends ModFluid
{
    public Fuelium()
    {
        super(ModFluids.FUELIUM, ModFluids.FLOWING_FUELIUM, ModBlocks.FUELIUM, 900, 900, 0.5F, 148, 242, 45);
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.FUELIUM_BUCKET.get();
    }

    public static class Source extends Fuelium
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

    public static class Flowing extends Fuelium
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
