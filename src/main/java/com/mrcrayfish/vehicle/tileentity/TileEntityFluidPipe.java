package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.BlockFluidPipe;
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
public class TileEntityFluidPipe extends TileFluidHandler implements ITickable
{
    private static final int CAPACITY = 10;

    public TileEntityFluidPipe()
    {
        tank = new FluidTank(CAPACITY);
    }

    @Override
    public void update()
    {
        if(tank.getFluid() == null)
            return;

        if(world.isBlockPowered(pos))
            return;

        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockFluidPipe.FACING);
        TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
        if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            if(handler != null)
            {
                FluidStack drained = tank.drain(CAPACITY, false);
                if(drained != null && drained.amount > 0)
                {
                    int filled = handler.fill(drained, false);
                    if(filled > 0)
                    {
                        drained = tank.drain(filled, true);
                        handler.fill(drained, true);
                    }
                }
            }
        }
    }
}
