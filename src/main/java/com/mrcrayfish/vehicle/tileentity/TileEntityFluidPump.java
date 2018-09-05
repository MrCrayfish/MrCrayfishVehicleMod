package com.mrcrayfish.vehicle.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.mrcrayfish.vehicle.block.BlockFluidPipe;
import com.mrcrayfish.vehicle.block.BlockFluidPump;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidPump extends TileFluidHandler implements ITickable
{
    private static final int CAPACITY = 500;
    private static final int TRANSFER_AMOUNT = 10;

    public TileEntityFluidPump()
    {
        tank = new FluidTank(CAPACITY);
    }

    @Override
    public void update()
    {
        if(!world.isBlockPowered(pos))
            return;

        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockFluidPump.FACING);
        TileEntity tileEntity = world.getTileEntity(pos.offset(facing.getOpposite()));
        if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            if(handler != null)
            {
                FluidUtils.transferFluid(handler, tank, TRANSFER_AMOUNT);
            }
        }

        // Collect connections
        state = state.getActualState(world, pos);
        List<IFluidHandler> fluidHandlers = new ArrayList<>();
        for(EnumFacing face : EnumFacing.VALUES)
        {
            if(state.getValue(BlockFluidPump.CONNECTED_PIPES[face.getIndex()]))
            {
                tileEntity = world.getTileEntity(pos.offset(face));
                if(tileEntity != null)
                {
                    IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
                    if(handler != null)
                    {
                        fluidHandlers.add(handler);
                    }
                }
            }
        }

        // Return if no connections, or return and transfer full amount if one connection
        int count = fluidHandlers.size();
        if(count <= 1)
        {
            if (count == 1)
            {
                FluidUtils.transferFluid(tank, fluidHandlers.get(0), TRANSFER_AMOUNT);
            }
            return;
        }

        // Evenly distribute truncated proportion to all connections
        int remainder = Math.min(tank.getFluidAmount(), TRANSFER_AMOUNT * count);
        int amount = remainder / count;
        int filled;
        if(amount > 0)
        {
            ListIterator<IFluidHandler> iterator = fluidHandlers.listIterator();
            while(iterator.hasNext())
            {
                if(FluidUtils.transferFluid(tank, iterator.next(), amount) < amount)
                {
                    iterator.remove();
                }
            }
        }

        // Randomly distribute to the remaining non-full connections the proportion that would otherwise be lost in the above truncation
        remainder %= count;
        if(fluidHandlers.size() == 1)
        {
            FluidUtils.transferFluid(tank, fluidHandlers.get(0), remainder);
        }
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
}
