package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class GasPumpTankTileEntity extends TileFluidHandlerSynced
{
    public GasPumpTankTileEntity()
    {
        super(ModTileEntities.GAS_PUMP_TANK, FluidAttributes.BUCKET_VOLUME * 50, stack -> stack.getFluid() == ModFluids.FUELIUM);
    }
}