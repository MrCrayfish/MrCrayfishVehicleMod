package com.mrcrayfish.vehicle.tileentity;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.TileFluidHandler;

/**
 * Author: MrCrayfish
 */
public class TileEntityFuelDrum extends TileFluidHandler
{
    public TileEntityFuelDrum() {}

    public TileEntityFuelDrum(int capacity)
    {
        tank = new FluidTank(capacity);
    }
}
