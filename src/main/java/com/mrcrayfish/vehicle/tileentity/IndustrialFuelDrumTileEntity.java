package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;

/**
 * Author: MrCrayfish
 */
public class IndustrialFuelDrumTileEntity extends FuelDrumTileEntity
{
    public IndustrialFuelDrumTileEntity()
    {
        super(ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), ModBlocks.INDUSTRIAL_FUEL_DRUM.get().getCapacity());
    }
}
