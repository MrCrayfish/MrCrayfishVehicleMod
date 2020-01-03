package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

/**
 * Author: MrCrayfish
 */
public abstract class BlazeJuice extends ForgeFlowingFluid
{
    public BlazeJuice()
    {
        super(new Properties(() -> ModFluids.BLAZE_JUICE, () -> ModFluids.FLOWING_BLAZE_JUICE, FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_still"), new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_flowing")).viscosity(800)));
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.BLAZE_JUICE_BUCKET;
    }

    public static class Source extends BlazeJuice
    {
        public Source()
        {
            this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "blaze_juice"));
        }

        @Override
        public boolean isSource(IFluidState state)
        {
            return true;
        }

        @Override
        public int getLevel(IFluidState p_207192_1_)
        {
            return 8;
        }
    }

    public static class Flowing extends BlazeJuice
    {
        public Flowing()
        {
            this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "flowing_blaze_juice"));
        }

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
