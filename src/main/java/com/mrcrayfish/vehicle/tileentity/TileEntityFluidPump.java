package com.mrcrayfish.vehicle.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.vehicle.block.BlockFluidPump;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidPump extends TileEntityFluidPipe
{
    @Override
    public void update()
    {
        if(!world.isBlockPowered(pos))
            return;

        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockFluidPump.FACING);

        // Collect connections
        state = state.getActualState(world, pos);
        List<IFluidHandler> fluidHandlers = new ArrayList<>();
        for(EnumFacing face : EnumFacing.VALUES)
        {
            if(!disabledConnections[face.getIndex()] && state.getValue(BlockFluidPump.CONNECTED_PIPES[face.getIndex()]))
            {
                IFluidHandler handler = getConnectedFluidHandler(face);
                if (handler != null)
                {
                    fluidHandlers.add(handler);
                }
            }
        }

        int outputCount = fluidHandlers.size();
        if(outputCount == 0)
            return;

        TileEntity tileEntity = world.getTileEntity(pos.offset(facing.getOpposite()));
        if(tileEntity != null)
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            if(handler != null)
            {
                FluidUtils.transferFluid(handler, tank, transferAmount);
            }
        }

        // Return and transfer full amount if one connection
        if (outputCount == 1)
        {
            FluidUtils.transferFluid(tank, fluidHandlers.get(0), transferAmount);
            return;
        }

        // Evenly distribute truncated proportion to all connections
        int remainder = Math.min(tank.getFluidAmount(), transferAmount * outputCount);
        int amount = remainder / outputCount;
        if(amount > 0)
        {
            fluidHandlers.removeIf(iFluidHandler -> FluidUtils.transferFluid(tank, iFluidHandler, amount) < amount);
        }

        // Randomly distribute to the remaining non-full connections the proportion that would otherwise be lost in the above truncation
        remainder %= outputCount;
        if(fluidHandlers.size() == 1)
        {
            FluidUtils.transferFluid(tank, fluidHandlers.get(0), remainder);
        }

        int filled;
        for(int i = 0; i < remainder && !fluidHandlers.isEmpty(); i++)
        {
            int index = world.rand.nextInt(fluidHandlers.size());
            filled = FluidUtils.transferFluid(tank, fluidHandlers.get(index), 1);
            remainder -= filled;
            if(filled == 0)
            {
                fluidHandlers.remove(index);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);
    }
}
