package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles updating the disabled state of pipes. This runs after everything has ticked
 * to avoid race conditions.
 *
 * Author: MrCrayfish
 */
public class FluidNetworkHandler
{
    private static FluidNetworkHandler instance;

    public static FluidNetworkHandler instance()
    {
        if(instance == null)
        {
            instance = new FluidNetworkHandler();
        }
        return instance;
    }

    private boolean dirty = false;
    private Map<RegistryKey<World>, Set<BlockPos>> pipeUpdateMap = new HashMap<>();

    private FluidNetworkHandler() {}

    public void addPipeForUpdate(PipeTileEntity tileEntity)
    {
        if(!(tileEntity instanceof PumpTileEntity))
        {
            this.dirty = true;
            this.pipeUpdateMap.computeIfAbsent(tileEntity.getLevel().dimension(), key -> new HashSet<>()).add(tileEntity.getBlockPos());
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.WorldTickEvent event)
    {
        if(!this.dirty)
            return;

        if(event.phase != TickEvent.Phase.END)
            return;

        Set<BlockPos> positions = this.pipeUpdateMap.remove(event.world.dimension());
        if(positions != null)
        {
            positions.forEach(pos ->
            {
                TileEntity tileEntity = event.world.getBlockEntity(pos);
                if(tileEntity instanceof PipeTileEntity)
                {
                    PipeTileEntity pipeTileEntity = (PipeTileEntity) tileEntity;
                    BlockState state = pipeTileEntity.getBlockState();
                    boolean disabled = pipeTileEntity.getPumps().isEmpty() || event.world.hasNeighborSignal(pos);
                    event.world.setBlock(pos, state.setValue(FluidPipeBlock.DISABLED, disabled), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD);
                }
            });
        }

        if(this.pipeUpdateMap.isEmpty())
        {
            this.dirty = false;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        this.dirty = false;
    }
}
