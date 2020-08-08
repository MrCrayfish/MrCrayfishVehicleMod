package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

/**
 * Author: MrCrayfish
 */
public abstract class BlazeJuice extends ForgeFlowingFluid
{
    public BlazeJuice()
    {
        super(new Properties(() -> ModFluids.BLAZE_JUICE.get(), () -> ModFluids.FLOWING_BLAZE_JUICE.get(), FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_still"), new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_flowing")).viscosity(800).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)).block(() -> ModBlocks.BLAZE_JUICE.get()));
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.BLAZE_JUICE_BUCKET.get();
    }

    public static class Source extends BlazeJuice
    {
        @Override
        public boolean isSource(FluidState state)
        {
            return true;
        }

        @Override
        public int getLevel(FluidState state)
        {
            return 8;
        }
    }

    public static class Flowing extends BlazeJuice
    {
        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
        {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(FluidState state)
        {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return false;
        }
    }
}
