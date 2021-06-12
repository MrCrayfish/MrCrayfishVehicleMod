package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FluidPipeTileEntity extends TileFluidHandlerSynced implements ITickableTileEntity
{
    protected int transferAmount;
    protected boolean[] disabledConnections;

    @SuppressWarnings("ConstantConditions")
    public FluidPipeTileEntity()
    {
        this(ModTileEntities.FLUID_PIPE.get(), Config.SERVER.pipeCapacity.get());
    }

    public FluidPipeTileEntity(TileEntityType<?> tileEntityType, int capacity)
    {
        super(tileEntityType, capacity);
        this.transferAmount = Config.SERVER.pipeTransferAmount.get();
        this.disabledConnections = new boolean[Direction.values().length];
    }

    public static boolean[] getDisabledConnections(FluidPipeTileEntity pipe)
    {
        return pipe != null ? pipe.getDisabledConnections() : new boolean[Direction.values().length];
    }

    public boolean[] getDisabledConnections()
    {
        return this.disabledConnections;
    }

    public boolean isConnectionDisabled(Direction facing)
    {
        return this.disabledConnections[facing.getIndex()];
    }

    private void setConnectionDisabled(int indexFacing, boolean disabled)
    {
        this.disabledConnections[indexFacing] = disabled;
        this.syncDisabledConnections();
    }

    public void setConnectionDisabled(Direction facing, boolean disabled)
    {
        this.setConnectionDisabled(facing.getIndex(), disabled);
    }

    @Override
    public void tick()
    {
        if(this.world == null)
            return;

        if(this.tank.getFluid().isEmpty())
            return;

        if(this.world.isBlockPowered(pos))
            return;

        IFluidHandler handler = this.getConnectedFluidHandler(this.world.getBlockState(this.pos).get(FluidPipeBlock.DIRECTION));
        if(handler != null)
        {
            FluidUtils.transferFluid(this.tank, handler, this.transferAmount);
        }
    }

    @Nullable
    protected IFluidHandler getConnectedFluidHandler(Direction facing)
    {
        BlockPos adjacentPos = this.pos.offset(facing);
        TileEntity tileEntity = this.world.getTileEntity(adjacentPos);
        if(tileEntity != null)
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
            if(handler != null)
            {
                BlockState adjacentState = this.world.getBlockState(adjacentPos);
                if(adjacentState.getBlock() instanceof FluidPipeBlock)
                {
                    if(!adjacentState.get(FluidPipeBlock.CONNECTED_PIPES[facing.getOpposite().getIndex()]) || (tileEntity instanceof FluidPipeTileEntity && ((FluidPipeTileEntity) tileEntity).isConnectionDisabled(facing.getOpposite())))
                    {
                        return null;
                    }
                }
                return handler;
            }
        }

        List<Entity> fluidEntities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(adjacentPos), entity -> entity != null && entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent());
        if(!fluidEntities.isEmpty())
        {
            return fluidEntities.get(0).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
        }

        return null;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
        if(compound.contains("DisabledConnections", Constants.NBT.TAG_BYTE_ARRAY))
        {
            byte[] connections = compound.getByteArray("DisabledConnections");
            for(int i = 0; i < connections.length; i++)
            {
                this.disabledConnections[i] = connections[i] == (byte) 1;
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        this.writeConnections(compound);
        return super.write(compound);
    }

    private void writeConnections(CompoundNBT compound)
    {
        byte[] connections = new byte[this.disabledConnections.length];
        for(int i = 0; i < connections.length; i++)
        {
            connections[i] = (byte) (this.disabledConnections[i] ? 1 : 0);
        }
        compound.putByteArray("DisabledConnections", connections);
    }

    private void syncDisabledConnections()
    {
        if(this.world != null && !this.world.isRemote)
        {
            CompoundNBT compound = new CompoundNBT();
            this.writeConnections(compound);
            TileEntityUtil.sendUpdatePacket(this, super.write(compound));
        }
    }
}
