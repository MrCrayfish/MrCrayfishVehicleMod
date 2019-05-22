package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Author: MrCrayfish
 */
public class TileEntityGasPumpTank extends TileFluidHandlerSynced
{
    public TileEntityGasPumpTank()
    {
        tank = new FluidTank(Fluid.BUCKET_VOLUME * 50)
        {
            @Override
            protected void onContentsChanged()
            {
                syncToClient();
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid)
            {
                return fluid.getFluid() == ModFluids.FUELIUM;
            }
        };
        tank.setCanFill(true);
        tank.setCanDrain(true);
    }

    public FluidTank getFluidTank()
    {
        return tank;
    }
}