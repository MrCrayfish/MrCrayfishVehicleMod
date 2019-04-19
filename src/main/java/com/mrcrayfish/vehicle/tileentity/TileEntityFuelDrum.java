package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidTank;

/**
 * Author: MrCrayfish
 */
public class TileEntityFuelDrum extends TileFluidHandlerSynced
{
    public TileEntityFuelDrum()
    {
        tank = new FluidTank(0)
        {
            @Override
            protected void onContentsChanged()
            {
                syncToClient();
            }
        };
        tank.setCanFill(true);
        tank.setCanDrain(true);
    }

    public TileEntityFuelDrum(int capacity)
    {
        this();
        this.setCapacity(capacity);
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

    private void setCapacity(int capacity)
    {
        tank.setCapacity(capacity);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);
        if(tag.hasKey("capacity", Constants.NBT.TAG_INT))
        {
            this.setCapacity(tag.getInteger("capacity"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("capacity", tank.getCapacity());
        return tag;
    }

}
