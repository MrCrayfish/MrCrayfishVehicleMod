package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class FuelDrumTileEntity extends TileFluidHandlerSynced
{
    public FuelDrumTileEntity()
    {
        super(ModTileEntities.FUEL_DRUM, 0);
    }

    public FuelDrumTileEntity(int capacity)
    {
        super(ModTileEntities.FUEL_DRUM, capacity);
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

    private void setCapacity(int capacity)
    {
        this.tank.setCapacity(capacity);
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        if(compound.contains("Capacity", Constants.NBT.TAG_INT))
        {
            this.setCapacity(compound.getInt("Capacity"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putInt("Capacity", this.tank.getCapacity());
        return super.write(compound);
    }
}
