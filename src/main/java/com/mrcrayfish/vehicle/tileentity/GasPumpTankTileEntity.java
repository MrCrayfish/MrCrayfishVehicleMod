package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModTileEntities;

/**
 * Author: MrCrayfish
 */
public class GasPumpTankTileEntity extends TileFluidHandlerSynced
{
    public GasPumpTankTileEntity()
    {
        super(ModTileEntities.GAS_PUMP_TANK, Config.SERVER.gasPumpCapacity.get(), stack -> stack.getFluid() == ModFluids.FUELIUM);
    }
}