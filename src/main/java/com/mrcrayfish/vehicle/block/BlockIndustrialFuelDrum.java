package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.IndustrialFuelDrumTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockIndustrialFuelDrum extends BlockFuelDrum
{
    public BlockIndustrialFuelDrum()
    {
        super(Names.Block.INDUSTRIAL_FUEL_DRUM);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new IndustrialFuelDrumTileEntity();
    }
}
