package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class PipeTileEntity extends TileEntity
{
    protected Set<BlockPos> pumps = new HashSet<>();
    protected boolean[] disabledConnections = new boolean[Direction.values().length];

    public PipeTileEntity()
    {
        super(ModTileEntities.FLUID_PIPE.get());
    }

    public PipeTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
    }

    public boolean[] getDisabledConnections()
    {
        return this.disabledConnections;
    }

    public void setConnectionState(Direction direction, boolean state)
    {
        this.disabledConnections[direction.get3DDataValue()] = state;
    }

    public boolean isConnectionDisabled(Direction direction)
    {
        return this.disabledConnections[direction.get3DDataValue()];
    }

    public void addPump(BlockPos pos)
    {
        this.pumps.add(pos);
    }

    public void removePump(BlockPos pos)
    {
        this.pumps.remove(pos);
    }

    public Set<BlockPos> getPumps()
    {
        return this.pumps;
    }
}
