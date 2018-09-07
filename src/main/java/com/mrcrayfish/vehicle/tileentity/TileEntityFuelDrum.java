package com.mrcrayfish.vehicle.tileentity;

import net.minecraftforge.fluids.FluidTank;

/**
 * Author: MrCrayfish
 */
public class TileEntityFuelDrum extends TileFluidHandlerSynced
{
    public TileEntityFuelDrum() {}

    public TileEntityFuelDrum(int capacity)
    {
        tank = new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                syncToClient();
            }
        };
    }

    public FluidTank getFluidTank()
    {
        return tank;
    }

    public boolean hasFluid()
    {
        return tank.getFluid() != null;
    }

    public int getAmount()
    {
        return tank.getFluidAmount();
    }

    public int getCapacity()
    {
        return tank.getCapacity();
    }
}
