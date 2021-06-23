package com.mrcrayfish.vehicle.tileentity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class PumpTileEntity extends PipeTileEntity implements ITickableTileEntity
{
    private boolean validatedNetwork;
    private Map<BlockPos, PipeNode> fluidNetwork = new HashMap<>();
    private List<Pair<BlockPos, Direction>> fluidHandlers = new ArrayList<>();

    public PumpTileEntity()
    {
        super(ModTileEntities.FLUID_PUMP.get());
    }

    @Override
    public void tick()
    {
        if(this.level != null && !this.level.isClientSide())
        {
            if(!this.validatedNetwork)
            {
                this.validatedNetwork = true;
                this.generatePipeNetwork();
            }

            this.pumpFluid();
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

    private void pumpFluid()
    {
        if(this.fluidHandlers.isEmpty() || this.level == null)
            return;

        List<IFluidHandler> handlers = new ArrayList<>();
        this.fluidHandlers.forEach(pair ->
        {
            if(this.level.isLoaded(pair.getLeft()))
            {
                TileEntity tileEntity = this.level.getBlockEntity(pair.getLeft());
                if(tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, pair.getRight()).isPresent())
                {
                    Optional<IFluidHandler> handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, pair.getRight()).resolve();
                    handler.ifPresent(handlers::add);
                }
            }
        });

        if(handlers.isEmpty())
            return;

        BlockState selfState = this.getBlockState();
        Direction direction = selfState.getValue(FluidPumpBlock.DIRECTION);
        TileEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(direction.getOpposite()));
        if(tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).isPresent())
        {
            Optional<IFluidHandler> handlerOptional = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).resolve();
            if(!handlerOptional.isPresent())
                return;

            IFluidHandler handler = handlerOptional.get();
            int outputCount = handlers.size();
            int remainder = Math.min(handler.getFluidInTank(0).getAmount(), Config.SERVER.pumpTransferAmount.get());
            int amount = remainder / outputCount;
            if(amount > 0)
            {
                handlers.removeIf(targetHandler -> FluidUtils.transferFluid(handler, targetHandler, amount) < amount);
            }

            // Randomly distribute to the remaining non-full connections the proportion that would otherwise be lost in the above truncation
            remainder %= outputCount;
            if(handlers.size() == 1)
            {
                FluidUtils.transferFluid(handler, handlers.get(0), remainder);
            }

            int filled;
            for(int i = 0; i < remainder && !handlers.isEmpty(); i++)
            {
                int index = this.level.random.nextInt(handlers.size());
                filled = FluidUtils.transferFluid(handler, handlers.get(index), 1);
                remainder -= filled;
                if(filled == 0)
                {
                    handlers.remove(index);
                }
            }
        }
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
                if(selfState.getBlock() instanceof FluidPipeBlock)
                {
                    if(selfState.getValue(FluidPipeBlock.POWERED))
                        continue;

                    if(!selfState.getValue(FluidPipeBlock.CONNECTED_PIPES[direction.get3DDataValue()]))
                        continue;
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
                    TileEntity selfTileEntity = this.level.getBlockEntity(pos);
                    if(selfTileEntity instanceof PipeTileEntity)
                    {
                        PipeTileEntity pipeTileEntity = (PipeTileEntity) selfTileEntity;
                        pipeTileEntity.addPump(this.worldPosition);
                        node.tileEntity = new WeakReference<>(pipeTileEntity);
                    }

                    if(state.getValue(FluidPipeBlock.POWERED))
                        continue;

                    BlockPos relativePos = pos.relative(direction);
                    TileEntity relativeTileEntity = this.level.getBlockEntity(relativePos);
                    if(relativeTileEntity != null && relativeTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent())
                    {
                        this.fluidHandlers.add(Pair.of(relativePos, direction.getOpposite()));
                    }
                }
            }
        });

        System.out.println("Generated fluid network. Found " + this.fluidNetwork.size() + " pipes and " + this.fluidHandlers.size() + " fluid handlers!");
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
        private WeakReference<PipeTileEntity> tileEntity;
    }
}
