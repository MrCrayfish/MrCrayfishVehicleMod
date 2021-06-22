package com.mrcrayfish.vehicle.tileentity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class PumpTileEntity extends PipeTileEntity implements ITickableTileEntity
{
    private boolean validatedNetwork;
    private Map<BlockPos, PipeNode> fluidNetwork = new HashMap<>();
    private List<BlockPos> fluidHandlers = new ArrayList<>();

    public PumpTileEntity()
    {
        super(ModTileEntities.FLUID_PUMP.get());
    }

    @Override
    public void tick()
    {
        if(!this.validatedNetwork && this.level != null && !this.level.isClientSide())
        {
            this.validatedNetwork = true;
            this.generatePipeNetwork();
        }
    }

    public Map<BlockPos, PipeNode> getFluidNetwork()
    {
        return ImmutableMap.copyOf(this.fluidNetwork);
    }

    public void invalidatePipeNetwork()
    {
        this.validatedNetwork = false;
    }

    // This can probably be improved...
    private void generatePipeNetwork()
    {
        Preconditions.checkNotNull(this.level);

        // Removes the pump from the old network pipes
        this.removePumpFromPipes();

        this.fluidHandlers.clear();
        this.fluidNetwork.clear();

        // Finds all the pipes in the network
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(this.worldPosition);
        while(!queue.isEmpty())
        {
            BlockPos pos = queue.poll();
            for(Direction direction : Direction.values())
            {
                BlockPos relativePos = pos.relative(direction);
                if(visited.contains(relativePos))
                    continue;

                BlockState selfState = this.level.getBlockState(pos);
                if(selfState.getBlock() == ModBlocks.FLUID_PIPE.get())
                {
                    if(!selfState.getValue(FluidPipeBlock.CONNECTED_PIPES[direction.get3DDataValue()]))
                    {
                        continue;
                    }
                }

                BlockState relativeState = this.level.getBlockState(relativePos);
                if(relativeState.getBlock() == ModBlocks.FLUID_PIPE.get())
                {
                    if(relativeState.getValue(FluidPipeBlock.CONNECTED_PIPES[direction.getOpposite().get3DDataValue()]))
                    {
                        visited.add(relativePos);
                        queue.add(relativePos);
                    }
                }
            }
        }

        // Initialise pipe nodes
        visited.forEach(pos -> this.fluidNetwork.put(pos, new PipeNode()));

        // Link pipe nodes
        this.fluidNetwork.forEach((pos, node) ->
        {
            BlockState state = this.level.getBlockState(pos);
            for(Direction direction : Direction.values())
            {
                if(state.getValue(FluidPipeBlock.CONNECTED_PIPES[direction.get3DDataValue()]))
                {
                    TileEntity tileEntity = this.level.getBlockEntity(pos);
                    if(tileEntity instanceof PipeTileEntity)
                    {
                        PipeTileEntity pipeTileEntity = (PipeTileEntity) tileEntity;
                        pipeTileEntity.addPump(this.worldPosition);
                        node.tileEntity = new WeakReference<>(pipeTileEntity);
                    }

                    BlockPos relativePos = pos.relative(direction);
                    BlockState relativeState = this.level.getBlockState(relativePos);
                    if(relativeState.getBlock() == ModBlocks.FLUID_PIPE.get())
                    {
                        node.nodes[direction.get3DDataValue()] = this.fluidNetwork.get(relativePos);
                    }
                    else
                    {
                        TileEntity relativeTileEntity = this.level.getBlockEntity(relativePos);
                        if(relativeTileEntity != null && relativeTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent())
                        {
                            this.fluidHandlers.add(relativePos);
                        }
                    }
                }
            }
        });

        System.out.println("Generated fluid network. Found " + this.fluidNetwork.size() + " nodes!");
    }

    public void removePumpFromPipes()
    {
        this.fluidNetwork.forEach((pos, node) ->
        {
            PipeTileEntity tileEntity = node.tileEntity.get();
            if(tileEntity != null)
            {
                tileEntity.removePump(this.worldPosition);
            }
        });
    }

    private static class PipeNode
    {
        // There is a finite amount of possible vertices
        private PipeNode[] nodes = new PipeNode[Direction.values().length];
        private WeakReference<PipeTileEntity> tileEntity;
    }
}
