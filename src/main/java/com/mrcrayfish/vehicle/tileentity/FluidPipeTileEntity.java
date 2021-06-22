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
        return this.disabledConnections[facing.get3DDataValue()];
    }

    private void setConnectionDisabled(int indexFacing, boolean disabled)
    {
        this.disabledConnections[indexFacing] = disabled;
        this.syncDisabledConnections();
    }

    public void setConnectionDisabled(Direction facing, boolean disabled)
    {
        this.setConnectionDisabled(facing.get3DDataValue(), disabled);
    }

    @Override
    public void tick()
    {
        if(this.level == null)
            return;

        if(this.tank.getFluid().isEmpty())
            return;

        if(this.level.hasNeighborSignal(worldPosition))
            return;

        /*IFluidHandler handler = this.getConnectedFluidHandler(this.level.getBlockState(this.worldPosition).getValue(FluidPipeBlock.DIRECTION));
        if(handler != null)
        {
            FluidUtils.transferFluid(this.tank, handler, this.transferAmount);
        }*/
    }

    @Nullable
    protected IFluidHandler getConnectedFluidHandler(Direction facing)
    {
        BlockPos adjacentPos = this.worldPosition.relative(facing);
        TileEntity tileEntity = this.level.getBlockEntity(adjacentPos);
        if(tileEntity != null)
        {
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
            if(handler != null)
            {
                BlockState adjacentState = this.level.getBlockState(adjacentPos);
                if(adjacentState.getBlock() instanceof FluidPipeBlock)
                {
                    if(!adjacentState.getValue(FluidPipeBlock.CONNECTED_PIPES[facing.getOpposite().get3DDataValue()]) || (tileEntity instanceof FluidPipeTileEntity && ((FluidPipeTileEntity) tileEntity).isConnectionDisabled(facing.getOpposite())))
                    {
                        return null;
                    }
                }
                return handler;
            }
        }

        List<Entity> fluidEntities = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(adjacentPos), entity -> entity != null && entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent());
        if(!fluidEntities.isEmpty())
        {
            return fluidEntities.get(0).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
        }

        return null;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound)
    {
        super.load(state, compound);
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
    public CompoundNBT save(CompoundNBT compound)
    {
        this.writeConnections(compound);
        return super.save(compound);
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
        if(this.level != null && !this.level.isClientSide)
        {
            CompoundNBT compound = new CompoundNBT();
            this.writeConnections(compound);
            TileEntityUtil.sendUpdatePacket(this, super.save(compound));
        }
    }
}
