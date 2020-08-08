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
public abstract class Fuelium extends ForgeFlowingFluid
{
    public Fuelium()
    {
        super(new Properties(() -> ModFluids.FUELIUM.get(), () -> ModFluids.FLOWING_FUELIUM.get(), FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "block/fuelium_still"), new ResourceLocation(Reference.MOD_ID, "block/fuelium_flowing")).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY).density(900).viscosity(900)).block(() -> ModBlocks.FUELIUM.get()));
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.FUELIUM_BUCKET.get();
    }

    public static class Source extends Fuelium
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

    public static class Flowing extends Fuelium
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
