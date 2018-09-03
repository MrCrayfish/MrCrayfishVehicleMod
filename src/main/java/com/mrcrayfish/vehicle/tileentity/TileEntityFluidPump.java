package com.mrcrayfish.vehicle.tileentity;

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

        state = state.getActualState(world, pos);
        for(EnumFacing face : EnumFacing.VALUES)
        {
            if(state.getValue(BlockFluidPump.CONNECTED_PIPES[face.getIndex()]))
            {
                tileEntity = world.getTileEntity(pos.offset(face));
                if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face))
                {
                    IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
                    if(handler != null)
                    {
                        FluidUtils.transferFluid(tank, handler, TRANSFER_AMOUNT);
                    }
                }
            }
        }
    }
}
