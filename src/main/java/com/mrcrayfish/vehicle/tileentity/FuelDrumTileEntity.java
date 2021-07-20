package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.tileentity.TileEntityType;

/**
 * Author: MrCrayfish
 */
public class FuelDrumTileEntity extends TileFluidHandlerSynced
{
    public FuelDrumTileEntity()
    {
        super(ModTileEntities.FUEL_DRUM.get(), ModBlocks.FUEL_DRUM.get().getCapacity());
    }

    public FuelDrumTileEntity(TileEntityType<?> tileEntityType, int capacity)
    {
        super(tileEntityType, capacity);
    }

    public boolean hasFluid()
    {
        return !this.tank.getFluid().isEmpty();
    }

    public int getAmount()
    {
        return this.tank.getFluidAmount();
    }

    public int getCapacity()
    {
        return this.tank.getCapacity();
    }
}
