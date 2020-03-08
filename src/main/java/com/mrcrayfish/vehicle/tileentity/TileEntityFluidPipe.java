package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.block.BlockFluidPipe;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityFluidPipe extends TileFluidHandlerSynced implements ITickable
{
    protected int capacity, transferAmount;
    protected boolean[] disabledConnections;

    public TileEntityFluidPipe()
    {
        this.capacity = 500;
        this.transferAmount = VehicleConfig.SERVER.pipeTransferAmount;
        this.tank = new FluidTank(this.capacity);
        this.disabledConnections = new boolean[EnumFacing.values().length];
    }

    public static boolean[] getDisabledConnections(TileEntityFluidPipe pipe)
    {
        return pipe != null ? pipe.getDisabledConnections() : new boolean[EnumFacing.values().length];
    }

    public boolean[] getDisabledConnections()
    {
        return disabledConnections;
    }

    public boolean isConnectionDisabled(int indexFacing)
    {
        return disabledConnections[indexFacing];
    }

    public boolean isConnectionDisabled(EnumFacing facing)
    {
        return disabledConnections[facing.getIndex()];
    }

    public void setConnectionDisabled(int indexFacing, boolean disabled)
    {
        disabledConnections[indexFacing] = disabled;
        syncToClient();
    }

    public void setConnectionDisabled(EnumFacing facing, boolean disabled)
    {
        setConnectionDisabled(facing.getIndex(), disabled);
    }

    @Override
    public void update()
    {
        if(tank.getFluid() == null)
            return;

        if(world.isBlockPowered(pos))
            return;

        IFluidHandler handler = getConnectedFluidHandler(world.getBlockState(pos).getValue(BlockFluidPipe.FACING));
        if (handler != null)
        {
            FluidUtils.transferFluid(tank, handler, transferAmount);
        }
    }

    @Nullable
    protected IFluidHandler getConnectedFluidHandler(EnumFacing facing)
    {
        BlockPos adjacentPos = pos.offset(facing);
        TileEntity tileEntity = world.getTileEntity(adjacentPos);
        if(tileEntity != null)
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if(handler != null)
            {
                IBlockState adjacentState = world.getBlockState(adjacentPos);
                adjacentState = adjacentState.getActualState(world, adjacentPos);
                if (adjacentState.getBlock() instanceof BlockFluidPipe)
                {
                    if (!adjacentState.getValue(BlockFluidPipe.CONNECTED_PIPES[facing.getOpposite().getIndex()])
                            || (tileEntity instanceof TileEntityFluidPipe && ((TileEntityFluidPipe) tileEntity).isConnectionDisabled(facing.getOpposite())))
                    {
                        return null;
                    }
                }
                return handler;
            }
        }

        List<Entity> fluidEntities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(adjacentPos), entity -> entity != null && entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
        if(!fluidEntities.isEmpty())
        {
            return fluidEntities.get(0).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);
        byte[] byteArr = tag.getByteArray("disabledConnections");
        for (int i = 0; i < byteArr.length; i++)
        {
            disabledConnections[i] = byteArr[i] == (byte) 1;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        byte[] byteArr = new byte[disabledConnections.length];
        for (int i = 0; i < byteArr.length; i++)
        {
            byteArr[i] = (byte) (disabledConnections[i] ? 1 : 0);
        }
        tag.setByteArray("disabledConnections", byteArr);
        return tag;
    }
}
